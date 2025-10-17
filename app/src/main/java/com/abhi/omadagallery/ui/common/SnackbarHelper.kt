package com.abhi.omadagallery.ui.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun RequireOnlineOrSnackbar(
    isOnline: Boolean,
    snackbarHost: SnackbarHostState,
    scope: CoroutineScope,
    block: () -> Unit
) {
    if (!isOnline) {
        scope.launch {
            snackbarHost.showSnackbar(
                message = "No internet connection",
                withDismissAction = true,
                duration = SnackbarDuration.Long
            )
        }
    } else {
        block()
    }
}