package sheridancollege.capstoneproject.mindmatrix.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import sheridancollege.capstoneproject.mindmatrix.data.Quiz
import sheridancollege.capstoneproject.mindmatrix.databinding.FragmentHomeBinding
import sheridancollege.capstoneproject.mindmatrix.ui.game.GamingActivity
import sheridancollege.capstoneproject.mindmatrix.ui.login.LoginActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val db = FirebaseFirestore.getInstance()

    var quizList: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email

        if (email != null) {

            db.collection("quizzes").get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        try {
                            // Get the question
                            val question = document.getString("question") ?: ""

                            // Validate if we have enough data to create a quiz
                            if (question.isNotEmpty()) {
                                quizList += 1
                            } else {
                                Log.e("QuizDataError", "Invalid data.")
                            }
                        } catch (e: Exception) {
                            Log.e("QuizError", "Error parsing quiz data", e)
                        }
                    }

                    db.collection("users").whereEqualTo("email", email).get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                val name = document.getString("name")
                                val userPoints = document.getString("points")

                                // Update the textviews with the database info
                                binding.txtWelcome.text = "Welcome $name"
                                binding.txtRank.text = userPoints
                                binding.txtChallengeN.text = quizList.toString() + " Questions"
                            }
                        }
                        .addOnFailureListener { exception ->
                            // if error
                            Log.w("Firestore", "Error: ", exception)
                        }
                }
        }

        val logout = binding.btLogout.setOnClickListener{
            logout()
        }

        val gaming = binding.btStart.setOnClickListener{
            game()
        }

        return root
    }


    private fun logout() {
        FirebaseAuth.getInstance().signOut() // Firebase logout
        goToLoginScreen() // Fuction to send the user back to the login page
    }

    private fun game() {
        goToGameScreen() // Fuction to send the user to the game page
    }

    private fun goToLoginScreen() {
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish() // Close the current activity
    }

    private fun goToGameScreen() {
        val intent = Intent(requireActivity(), GamingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish() // Fecha a Activity atual
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            goToLoginScreen() // If the user is not authenticate, return to the login screen
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}