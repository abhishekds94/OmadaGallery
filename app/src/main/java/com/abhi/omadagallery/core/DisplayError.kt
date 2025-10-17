package com.abhi.omadagallery.core

fun mapToDisplayError(message: String?): String {
    if (message == null) return "Something went wrong. Please try again!"

    return when {
        message.contains("Unable to resolve host", ignoreCase = true) ||
                message.contains("Network", ignoreCase = true) ||
                message.contains("Failed to connect", ignoreCase = true) ||
                message.contains("No internet", ignoreCase = true) -> {
            "No active internet. Please try again!"
        }

        else -> "Something went wrong. Please try again!"
    }
}