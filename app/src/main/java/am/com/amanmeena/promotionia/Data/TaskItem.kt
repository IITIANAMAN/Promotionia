package am.com.amanmeena.promotionia.Data

data class TaskItem(
    val id: String = "",
    val title: String = "",
    val link: String = "",
    val platform: String = "",
    val reward: Int = 0,
    val isActive: Boolean = true,
    val description: String? = null,
    val click: Int = 0
)
