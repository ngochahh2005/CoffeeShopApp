package com.example.coffeeshopapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.presentation.theme.rememberScreenInfo

@Composable
fun AuthScreenLogo(modifier: Modifier = Modifier) {
    val backgroundLogoHeight = rememberScreenInfo().logoHeight
    val logoHeight = backgroundLogoHeight * 0.9f

    Box(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(R.drawable.login_background),
            contentDescription = null,
            contentScale = ContentScale.Inside,
            modifier = Modifier.height(backgroundLogoHeight).align(Alignment.TopCenter)
        )
        Image(
            painter = painterResource(R.drawable.icon_login),
            contentDescription = null,
            modifier = Modifier
                .height(logoHeight)
                .align(Alignment.TopCenter)
                .padding(top = 5.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
@Preview(showSystemUi = true)
fun AuthScreenLogoPreview() {
    AuthScreenLogo()
}

@Composable
@Preview(showSystemUi = true, device = Devices.TABLET)
fun AuthScreenLogoPreview2() {
    AuthScreenLogo()
}