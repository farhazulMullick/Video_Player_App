package com.farhazulmullick.core_ui.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun YSpacer(gap: Dp){
    Spacer(modifier = Modifier.height(height = gap))
}