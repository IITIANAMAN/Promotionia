package am.com.amanmeena.promotionia


import am.com.amanmeena.promotionia.Viewmodels.MainViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import am.com.amanmeena.promotionia.ui.theme.PromotioniaTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = viewModel<MainViewModel>(
                factory = object : ViewModelProvider.Factory{
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MainViewModel() as T
                    }
                }
            )
            PromotioniaTheme {
                    MyAppNavigation(modifier = Modifier,viewModel)
            }
        }
    }
}
