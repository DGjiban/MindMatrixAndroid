package sheridancollege.capstoneproject.mindmatrix.ui.game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sheridancollege.capstoneproject.mindmatrix.R

class AnswerAdapter(
    private val answers: List<String>,
    private val onAnswerClick: (String) -> Unit // Callback for when an answer is clicked
) : RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>() {

    class AnswerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtAnswer: TextView = view.findViewById(R.id.txtAnswer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_answer, parent, false)
        return AnswerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val answer = answers[position]
        holder.txtAnswer.text = answer

        // Set click listener for each answer
        holder.itemView.setOnClickListener {
            onAnswerClick(answer)
        }
    }

    override fun getItemCount(): Int {
        return answers.size
    }
}
