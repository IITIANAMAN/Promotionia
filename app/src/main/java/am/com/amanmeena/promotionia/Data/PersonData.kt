data class PersonData(
    val uid: String = "",
    val name : String = "",
    val email: String = "",
    val number:String = "",
    val state:String = "",
    val accountFB: List<String> = emptyList(),
    val accountInsta: List<String> = emptyList(),
    val accountX: List<String> = emptyList(),
    val totalCoin: Int = 0,
    val totalCoinFb: Int = 0,
    val totalCoinInsta: Int = 0,
    val totalCoinX: Int = 0,
    val totalCoinMap: Int =0,
    val createdAt: Long = System.currentTimeMillis(),
    val completedTasks: Map<String, Map<String, List<String>>> = emptyMap()
)