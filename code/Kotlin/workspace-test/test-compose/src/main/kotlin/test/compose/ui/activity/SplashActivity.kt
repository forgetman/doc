package test.compose.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import vector.app.compose.ui.activity.SplashActivityEx
import vector.ext.startActivity

/**
 * @author yuansui
 * @since 2025/6/19
 */
class SplashActivity : SplashActivityEx() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Splash") },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "这是splash",
                    color = Color.White
                )
            }
        }
    }

    override fun passTo() {
        startActivity<MainActivity>()
    }
}