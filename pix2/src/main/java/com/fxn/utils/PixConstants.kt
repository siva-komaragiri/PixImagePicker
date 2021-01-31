package com.fxn.utils

import android.view.View

/**
 * @author : Akshay Sharma
 * @since : 31/1/21, Sun
 * akshay2211.github.io
 **/

const val PIX_OPTIONS = "PixOptions"
const val IMMERSIVE_FLAG_TIMEOUT = 500L

@Suppress("DEPRECATION")
const val FLAGS_FULLSCREEN = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
        // Hide the nav bar and status bar
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or// Hide nav bar
        View.SYSTEM_UI_FLAG_FULLSCREEN // Hide status bar