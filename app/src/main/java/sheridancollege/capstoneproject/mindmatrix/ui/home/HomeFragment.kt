package sheridancollege.capstoneproject.mindmatrix.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import sheridancollege.capstoneproject.mindmatrix.databinding.FragmentHomeBinding
import sheridancollege.capstoneproject.mindmatrix.ui.login.LoginActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val db = FirebaseFirestore.getInstance()

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
            db.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val name = document.getString("name")
                        val rank = document.getString("rank")

                        // Update the textviews with the database info
                        binding.txtWelcome.text = "Welcome $name"
                        binding.txtRank.text = rank
                    }
                }
                .addOnFailureListener { exception ->
                    // if error
                    Log.w("Firestore", "Error: ", exception)
                }
        }

        val logout = binding.btLogout.setOnClickListener{
            logout()
        }

        return root
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut() // Firebase logout
        goToLoginScreen() // Fuction to send the user back to the login page
    }

    private fun goToLoginScreen() {
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish() // Close the current activity
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