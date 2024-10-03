package sheridancollege.capstoneproject.mindmatrix.ui.ranking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sheridancollege.capstoneproject.mindmatrix.R
import sheridancollege.capstoneproject.mindmatrix.data.User

class RankAdapter(private var userList: List<User>) : RecyclerView.Adapter<RankAdapter.RankViewHolder>() {

    class RankViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserName: TextView = view.findViewById(R.id.txtUserName)
        val txtUserPoints: TextView = view.findViewById(R.id.txtUserPoints)
        val txtRankPosition: TextView = view.findViewById(R.id.txtRankPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rank, parent, false)
        return RankViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankViewHolder, position: Int) {
        val user = userList[position]
        holder.txtUserName.text = user.name
        holder.txtUserPoints.text = user.points.toString()
        holder.txtRankPosition.text = user.rank.toString() // Use original rank
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    // Update the list and notify the adapter
    fun updateList(newList: List<User>) {
        userList = newList
        notifyDataSetChanged() // Notify the adapter to refresh the view
    }
}
