package sheridancollege.capstoneproject.mindmatrix.ui.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import sheridancollege.capstoneproject.mindmatrix.databinding.FragmentRankingBinding
import sheridancollege.capstoneproject.mindmatrix.data.User

class RankingFragment : Fragment() {

    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private var userList = mutableListOf<User>() // Store the complete list of users
    private lateinit var rankAdapter: RankAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Fetch and display rank data
        fetchAndDisplayRank()

        // Set up search functionality
        setupSearch()

        return root
    }

    // Fetch and display rank data
    private fun fetchAndDisplayRank() {
        db.collection("users")
            .orderBy("points", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                userList.clear() // Clear the list to avoid duplicates

                var rank = 1
                for (document in documents) {
                    val name = document.getString("name") ?: "Unknown"
                    val userPoints = document.getString("points")

                    // Assign rank based on original list position
                    userList.add(User(name, userPoints, rank))
                    rank++ // Increment rank for the next user
                }

                // Now display the rank
                displayRank(userList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching rank", Toast.LENGTH_SHORT).show()
            }
    }

    // Set up and display the rank in the RecyclerView
    private fun displayRank(userList: List<User>) {
        rankAdapter = RankAdapter(userList)
        binding.rvRank.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRank.adapter = rankAdapter
    }

    // Set up the SearchView to filter results
    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the list based on user input
                filterRankList(newText)
                return true
            }
        })
    }

    // Function to filter the rank list based on the search input
    private fun filterRankList(query: String?) {
        val filteredList = if (!query.isNullOrEmpty()) {
            userList.filter { user ->
                user.name.contains(query, ignoreCase = true)
            }
        } else {
            userList // If query is empty, show the full list
        }

        // Update the adapter with the filtered list
        rankAdapter.updateList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
