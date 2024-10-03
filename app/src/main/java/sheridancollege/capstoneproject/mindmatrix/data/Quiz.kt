package sheridancollege.capstoneproject.mindmatrix.data

data class Quiz(
    val question: String = "",
    val answers: List<String> = emptyList(),
    val correctAnswer: String = ""
)
