package am.com.amanmeena.promotionia.Request

data class RequestForId(
    val accountHandel: String,
    val accountLink:String,
    val platform:String,
    val isAccepted: Boolean = false

)
