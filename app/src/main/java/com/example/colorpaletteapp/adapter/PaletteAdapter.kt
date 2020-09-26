package com.example.colorpaletteapp.adapter

import android.content.Context
import android.content.Intent
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.colorpaletteapp.ListActivity
import com.example.colorpaletteapp.PaletteActivity
import com.example.colorpaletteapp.R
import com.example.colorpaletteapp.data.PalettePost
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.palette_row.view.*

class PaletteAdapter : RecyclerView.Adapter<PaletteAdapter.ViewHolder> {

    companion object {
        private const val LIGHT_VIBRANT = "LIGHT VIBRANT"
        private const val VIBRANT = "VIBRANT"
        private const val DARK_VIBRANT = "DARK VIBRANT"
        private const val LIGHT_MUTED = "LIGHT MUTED"
        private const val MUTED = "MUTED"
        private const val DARK_MUTED = "DARK MUTED"
        private const val IMG_URL = "IMG_URL"
    }

    lateinit var context: Context
    var paletteLists = mutableListOf<PalettePost>()
    var paletteKeys = mutableListOf<String>()

    constructor(context: Context) : super() {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.palette_row, parent, false
        )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return paletteLists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var post = paletteLists.get(holder.adapterPosition)

        holder.lightVibrantSwatch.setBackgroundColor(post.lightVibrant)
        holder.vibrantSwatch.setBackgroundColor(post.vibrant)
        holder.darkVibrantSwatch.setBackgroundColor(post.darkVibrant)
        holder.lightMutedSwatch.setBackgroundColor(post.lightMuted)
        holder.mutedSwatch.setBackgroundColor(post.muted)
        holder.darkMutedSwatch.setBackgroundColor(post.darkMuted)

        holder.btnDelete.setOnClickListener {
            removePost(holder.adapterPosition)
        }

        Glide.with(context).load(post.imgUrl).into(holder.img)

        holder.btnMore.setOnClickListener {
            showPalettePage(post)
        }
    }

    private fun showPalettePage(pal: PalettePost) {
        var intentDetails = Intent()
        intentDetails.setClass(context, PaletteActivity::class.java)
        intentDetails.putExtra(LIGHT_VIBRANT, pal.lightVibrant)
        intentDetails.putExtra(VIBRANT, pal.vibrant)
        intentDetails.putExtra(DARK_VIBRANT, pal.darkVibrant)
        intentDetails.putExtra(LIGHT_MUTED, pal.lightMuted)
        intentDetails.putExtra(MUTED, pal.muted)
        intentDetails.putExtra(DARK_MUTED, pal.darkMuted)
        intentDetails.putExtra(IMG_URL, pal.imgUrl)
        context.startActivity(intentDetails)
    }

    fun addPalette(post: PalettePost, key: String) {
        paletteLists.add(post)
        paletteKeys.add(key)
        notifyItemInserted(paletteLists.lastIndex)
    }

    private fun removePost(index: Int) {
        FirebaseFirestore.getInstance().collection("palettes").document(paletteKeys[index]).delete()
        paletteKeys.removeAt(index)
        paletteLists.removeAt(index)
        notifyItemRemoved(index)
    }

    fun removePostByKey(key: String) {
        val index = paletteKeys.indexOf(key)
        if (index != -1) {
            paletteLists.removeAt(index)
            paletteKeys.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var lightVibrantSwatch = itemView.lightVibrantSwatch
        var vibrantSwatch = itemView.vibrantSwatch
        var darkVibrantSwatch = itemView.darkVibrantSwatch
        var lightMutedSwatch = itemView.lightMutedSwatch
        var mutedSwatch = itemView.mutedSwatch
        var darkMutedSwatch = itemView.darkMutedSwatch
        var img = itemView.ivPhoto
        var btnDelete = itemView.btnDelete
        var btnMore = itemView.btnMore
    }
}