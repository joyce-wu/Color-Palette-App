package com.example.colorpaletteapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_palette.*
import kotlinx.android.synthetic.main.palette_row.*

class PaletteActivity : AppCompatActivity() {

    companion object {
        private const val LIGHT_VIBRANT = "LIGHT VIBRANT"
        private const val VIBRANT = "VIBRANT"
        private const val DARK_VIBRANT = "DARK VIBRANT"
        private const val LIGHT_MUTED = "LIGHT MUTED"
        private const val MUTED = "MUTED"
        private const val DARK_MUTED = "DARK MUTED"
        private const val IMG_URL = "IMG_URL"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_palette)

        val lightVibrantSwatch = intent.getIntExtra(LIGHT_VIBRANT, 0)
        val vibrantSwatch = intent.getIntExtra(VIBRANT, 0)
        val darkVibrantSwatch = intent.getIntExtra(DARK_VIBRANT, 0)
        val lightMutedSwatch = intent.getIntExtra(LIGHT_MUTED, 0)
        val mutedSwatch = intent.getIntExtra(MUTED, 0)
        val darkMutedSwatch = intent.getIntExtra(DARK_MUTED, 0)

        lightVibrant.setBackgroundColor(lightVibrantSwatch)
        lightVibrant.setOnClickListener {
            lightVibrant.text = "Light Vibrant: ${lightVibrantSwatch.toString()}"
        }

        vibrant.setBackgroundColor(vibrantSwatch)
        vibrant.setOnClickListener {
            vibrant.text = "Vibrant: ${vibrantSwatch.toString()}"
        }

        darkVibrant.setBackgroundColor(darkVibrantSwatch)
        darkVibrant.setOnClickListener {
            darkVibrant.text = "Dark Vibrant: ${darkVibrantSwatch.toString()}"
        }

        lightMuted.setBackgroundColor(lightMutedSwatch)
        lightMuted.setOnClickListener {
            lightMuted.text = "Light Muted: ${lightMutedSwatch.toString()}"
        }

        muted.setBackgroundColor(mutedSwatch)
        muted.setOnClickListener {
            muted.text = "Muted: ${mutedSwatch.toString()}"
        }

        darkMuted.setBackgroundColor(darkMutedSwatch)
        darkMuted.setOnClickListener {
            darkMuted.text = "Muted: ${darkMutedSwatch.toString()}"
        }

        val imgURL = intent.getStringExtra(IMG_URL)
        Glide.with(this).load(imgURL).into(imgUpload)

    }

}