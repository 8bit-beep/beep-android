package com.test.beep_and.res.modifier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController


fun Modifier.clearFocusOnClick(
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController? = null
): Modifier = this.clickable(
    interactionSource = MutableInteractionSource(),
    indication = null
) {
    focusManager.clearFocus()
    keyboardController?.hide()
}

@Composable
fun FocusClearableContainer(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier.clearFocusOnClick(focusManager, keyboardController),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}