package sheridancollege.capstoneproject.mindmatrix.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import sheridancollege.capstoneproject.mindmatrix.R
import sheridancollege.capstoneproject.mindmatrix.databinding.FragmentRegisterBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private val firebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btRegisterRegister.setOnClickListener {
            val name = binding.tfNameRegister.text.toString().trim()
            val email = binding.tfEmailRegister.text.toString().trim()
            val password = binding.tfPasswordRegister.text.toString().trim()
            val retypePassword = binding.tfReTypePasswordRegister.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && retypePassword.isNotEmpty()) {
                if (password == retypePassword) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                addUser(name, email)

                                // Navigate to the Login fragment
                                findNavController().navigate(R.id.action_navigation_register_to_navigation_login)
                            } else {
                                // Show error message
                                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }

                } else {
                    Toast.makeText(context, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btCancel.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_register_to_navigation_login)
        }
    }

    private fun addUser(name: String, email: String) {
        try {
            val user: MutableMap<String, Any> = HashMap()
            user["name"] = name
            user["email"] = email
            user["birth"] = ""
            user["points"] = "0"

            firebaseFirestore.collection("users").document(email)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Added", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error adding the User ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment registerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}