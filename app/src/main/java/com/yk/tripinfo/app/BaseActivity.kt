package com.yk.tripinfo.app

import android.view.View
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {
    open fun getRootView() : View {
        throw Exception("Implement getBinding")
    }
}