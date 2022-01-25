package com.ilkcanyilmaz.finddeviceradar

import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    lateinit var radarView: RadarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setContentView(R.layout.activity_main)
        radarView = findViewById(R.id.radarview)

    }

    override fun onStart() {
        super.onStart()

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        val r = width.coerceAtMost(height)

        val i = r / 2f
        val j = (i - 1f) * 2.4f

        val btnFindDevice = ImageButton(applicationContext)

        btnFindDevice.setBackgroundColor(Color.BLUE)
        btnFindDevice.background =
            ContextCompat.getDrawable(applicationContext, R.drawable.shape_button)
        val lButtonParams: RelativeLayout.LayoutParams =
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )

        this.addContentView(btnFindDevice, lButtonParams)

        radarView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                btnFindDevice.x = radarView.centerX.toFloat() - btnFindDevice.width / 2
                btnFindDevice.y = radarView.centerY.toFloat() - btnFindDevice.height / 2
                radarView.viewTreeObserver.removeOnGlobalLayoutListener(this)

            }
        })


        btnFindDevice.setOnClickListener {
            radarView.deviceDistance = height / 2f - (-200..650).random()
        }
    }
}