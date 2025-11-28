package am.com.amanmeena.promotionia.Components

import am.com.amanmeena.promotionia.R
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable

fun UpdatesPager() {

    val pages = listOf(
        R.drawable.sample1,
        R.drawable.sample2,
        R.drawable.sample3
    )

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size }
    )

    val fixedHeight = 120.dp   // ✅ FIXED SIZE (change if you want)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {

        // ⭐ FIXED FRAME FOR IMAGE (Always same size)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(fixedHeight)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Image(
                    painter = painterResource(id = pages[page]),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop  // ⭐ Handles any image size safely
                )
            }
        }

        // PAGE INDICATORS
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 6.dp)
        ) {
            repeat(pages.size) { index ->
                Box(
                    Modifier
                        .size(10.dp)
                        .padding(3.dp)
                        .background(
                            if (pagerState.currentPage == index) Color.Black
                            else Color.LightGray,
                            CircleShape
                        )
                )
            }
        }
    }
}