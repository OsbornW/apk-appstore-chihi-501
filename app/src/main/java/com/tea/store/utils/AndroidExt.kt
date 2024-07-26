package com.tea.store.utils

import android.os.Build

fun isAndroidAtMost5_1(): Boolean {
    return Build.VERSION.SDK_INT <= 22
}

fun isAndroidVersionAtMost(version:Int): Boolean {
    return Build.VERSION.SDK_INT <= version
}