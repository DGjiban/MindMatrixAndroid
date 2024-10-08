package sheridancollege.capstoneproject.mindmatrix.ui.profile

import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import sheridancollege.capstoneproject.mindmatrix.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore // Initialize db Firestore here
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance() // db is now initialized here

        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email

        // Fetch user data when the view is created
        getuserdata(email)

        // Handle profile update when clicking the update button
        binding.btProfileUpdate.setOnClickListener {
            updateProfile(email)
        }

        // Handle password reset
        binding.btResetPassword.setOnClickListener {
            resetPassword()
        }

        return root
    }

    // Fetch user data from Firestore and pre-fill the fields
    private fun getuserdata(email: String?) {
        val tfName = binding.tfProfileName
        val tfEmail = binding.tfProfileEmail
        val tfPoints = binding.tfProfilePoints

        // Limit the input to 8 characters for yyyyMMdd format
        val tfBirth = binding.tfBirth
        tfBirth.filters = arrayOf(InputFilter.LengthFilter(10))

        if (email != null) {
            db.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val name = document.getString("name")
                        val birth = document.getString("birth")
                        val userEmail = document.getString("email")
                        val userPoints = document.getString("points")

                        // Update the textviews with the database info
                        tfName.setText(name)
                        tfBirth.setText(birth)
                        tfEmail.setText(userEmail)
                        tfPoints.setText(userPoints)
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle the error
                    Log.w("Firestore", "Error: ", exception)
                }
        }
    }

    // Update the user profile in Firestore
    private fun updateProfile(email: String?) {
        val name = binding.tfProfileName.text.toString().trim()
        var birth = binding.tfBirth.text.toString().trim()

        // Ensure the date is exactly 8 characters long before formatting
        if (birth.length == 8) {
            // Convert yyyyMMdd to yyyy/MM/dd
            val formattedDate =
                birth.substring(0, 4) + "/" + birth.substring(4, 6) + "/" + birth.substring(6, 8)
            birth = formattedDate
        } else if (birth.length == 10) {
            val parts = birth.split("/")
            birth = parts[0] + "/" + parts[1] + "/" + parts[2]
        }
        else {
            Toast.makeText(requireContext(), "Invalid date format", Toast.LENGTH_LONG)
                .show()
        }

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(birth)) {
            Toast.makeText(
                requireContext(),
                "Name and Birth fields cannot be empty",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (email != null) {
            // Update Firestore
            db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // If document exists, get the document ID and update the fields
                        for (document in documents) {
                            db.collection("users").document(document.id)
                                .update("name", name, "birth", birth)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_LONG).show()
                                    getuserdata(email) // Refresh the data after update
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Error updating profile",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                    } else {
                        Toast.makeText(requireContext(), "No user found with this email", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error fetching user", Toast.LENGTH_LONG)
                        .show()
                }
        }
    }

    // Reset the user's password
    private fun resetPassword() {
        val pwd1 = binding.tfResetPwd01.text.toString().trim()
        val pwd2 = binding.tfResetPwd02.text.toString().trim()

        if (TextUtils.isEmpty(pwd1) || pwd1 != pwd2) {
            Toast.makeText(requireContext(), "Please verify your password", Toast.LENGTH_LONG).show()
        } else {
            auth.currentUser?.updatePassword(pwd1)
                ?.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_LONG).show()
                        clearFields()
                    } else {
                        Toast.makeText(requireContext(), "Password not changed", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    // Clear the password reset fields after successful password change
    private fun clearFields() {
        binding.tfResetPwd01.text?.clear()
        binding.tfResetPwd02.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
