package com.farhazulmullick.core_ui.commoncomposable

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide

@Composable
fun YSpacer(gap: Dp){
    Spacer(modifier = Modifier.height(height = gap))
}