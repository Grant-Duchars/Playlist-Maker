package edu.rosehulman.ducharg.playlistmaker

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class PlaylistDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val itemName = itemView.findViewById<TextView>(R.id.playlist_detail_view_title)
    private val itemCard = itemView.findViewById<CardView>(R.id.playlist_detail_view_card)

    fun bind(item: String) {
        val info = item.split(",")
        itemName.text = info[1]
        when (info[2]) {
            "song" -> itemCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.colorYellow
                )
            )
            "album" -> itemCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.colorDarkYellow
                )
            )
        }
    }
}