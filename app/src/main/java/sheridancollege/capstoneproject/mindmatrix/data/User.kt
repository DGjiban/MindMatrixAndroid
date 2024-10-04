package sheridancollege.capstoneproject.mindmatrix.data

data class User(
    val name: String,
    val email: String,
    val birth: String,
    val points: Int,
    var rank: Int
)