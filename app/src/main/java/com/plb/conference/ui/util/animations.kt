package com.example.yourapp.ui.util

import android.app.Activity
import android.content.Intent
import androidx.core.app.ActivityOptionsCompat
import android.os.Bundle

object ActivityTransitions {
    fun Activity.startActivityWithAnimation(intent: Intent, bundle: Bundle? = null) {
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        startActivity(intent, bundle ?: options.toBundle())
    }

    fun Activity.finishWithAnimation() {
        finish()
        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }
}