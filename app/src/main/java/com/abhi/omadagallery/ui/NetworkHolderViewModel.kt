package com.abhi.omadagallery.ui

import androidx.lifecycle.ViewModel
import com.abhi.omadagallery.core.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NetworkHolderViewModel @Inject constructor(
    val monitor: NetworkMonitor
) : ViewModel()