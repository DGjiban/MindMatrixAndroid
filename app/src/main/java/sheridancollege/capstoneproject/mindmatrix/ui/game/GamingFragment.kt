package sheridancollege.capstoneproject.mindmatrix.ui.game

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import sheridancollege.capstoneproject.mindmatrix.MainActivity
import sheridancollege.capstoneproject.mindmatrix.databinding.FragmentGamingBinding
import sheridancollege.capstoneproject.mindmatrix.data.Quiz

class GamingFragment : Fragment() {

    private var _binding: FragmentGamingBinding? = null
    private val binding get() = _binding!! // Ensure this is used only after it's initialized

    // List to store quizzes
    private val quizList = mutableListOf<Quiz>()
    private var currentQuizIndex = 0 // Track the current quiz index
    var points: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGamingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()

        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email

        var userID: String = ""

        if (email != null) {
            db.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val userPoints = document.getString("points")?.toInt()
                        userID = document.id
                        // Update user points
                        if (userPoints != null) {
                            points += userPoints
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // if error
                    Log.w("Firestore", "Error: ", exception)
                }
        }

        db.collection("quizzes").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    try {
                        // Get the question
                        val question = document.getString("question") ?: ""

                        // Get the answers as a list of strings
                        val answers = document.get("answers") as? List<String> ?: emptyList()

                        // Get the correct answer
                        val correctAnswerText = document.getString("correctAnswerText") ?: ""

                        // Validate if we have enough data to create a quiz
                        if (question.isNotEmpty() && answers.isNotEmpty()) {
                            val quiz = Quiz(question, answers, correctAnswerText)
                            quizList.add(quiz)
                        } else {
                            Log.e("QuizDataError", "Invalid data: question or answers are missing.")
                        }
                    } catch (e: Exception) {
                        Log.e("QuizError", "Error parsing quiz data", e)
                    }
                }

                if (quizList.isNotEmpty()) {
                    // Shuffle the quiz list before displaying
                    quizList.shuffle()
                    // Display the first quiz
                    displayQuiz(currentQuizIndex)
                } else {
                    Toast.makeText(requireContext(), "No quizzes available", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error loading quizzes", Toast.LENGTH_SHORT).show()
                Log.e("FirestoreError", "Error fetching quizzes", exception)
            }

        val finish = binding.btnFinishQuiz.setOnClickListener {
            db.collection("users").document(userID)
                .update("points", points.toString())
                .addOnSuccessListener {
                    // Successfully updated
                    Toast.makeText(requireContext(), "Points updated successfully", Toast.LENGTH_SHORT).show()

                    // Navigate back to MainActivity after success
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the back stack
                    startActivity(intent)
                    requireActivity().finish() // Close the current activity
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    Toast.makeText(requireContext(), "Error updating points", Toast.LENGTH_SHORT).show()
                }
        }

        return root
    }

    // Function to display the quiz based on the current index
    private fun displayQuiz(index: Int) {
        if (_binding == null) {
            Log.e("NullBindingError", "Attempted to access binding after view was destroyed")
            return
        }

        if (index < quizList.size) {
            val quiz = quizList[index]

            // Update the UI with the question
            binding.txtQuestion.text = quiz.question

            // Set up RecyclerView with answers
            binding.rvAnswers.layoutManager = LinearLayoutManager(requireContext())
            binding.rvAnswers.adapter = AnswerAdapter(quiz.answers) { selectedAnswer ->
                checkAnswer(quiz.correctAnswer, selectedAnswer)
            }
        } else {
            Toast.makeText(requireContext(), "Quiz complete!", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to check if the answer is correct
    private fun checkAnswer(correctAnswer: String, selectedAnswer: String) {
        if (correctAnswer == "Correct answer: $selectedAnswer") {
            points++
            Toast.makeText(requireContext(), "Correct answer!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Wrong answer!", Toast.LENGTH_SHORT).show()
        }

        // Show the next quiz after the current one is answered
        currentQuizIndex++
        if (currentQuizIndex < quizList.size) {
            displayQuiz(currentQuizIndex)
        } else {
            Toast.makeText(requireContext(), "You have completed all quizzes!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Nullify the binding to avoid memory leaks
    }
}
