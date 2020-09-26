package com.example.colorpaletteapp.data

data class PalettePost (
    var imgUrl: String = "",
    var lightVibrant: Int = 0,
    var vibrant: Int = 0,
    var darkVibrant: Int = 0,
    var lightMuted: Int = 0,
    var muted: Int = 0,
    var darkMuted: Int = 0
)