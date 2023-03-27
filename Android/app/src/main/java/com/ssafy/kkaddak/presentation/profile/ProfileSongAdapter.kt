package com.ssafy.kkaddak.presentation.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.kkaddak.R
import com.ssafy.kkaddak.databinding.ItemProfileSongBinding
import com.ssafy.kkaddak.domain.entity.song.SongItem

class ProfileSongAdapter(
    private val onItemClicked: (songId: String) -> Unit,
    private val onRejectBadgeClicked: (songId: String) -> Unit
) : RecyclerView.Adapter<ProfileSongAdapter.ViewHolder>() {

    private var items: List<SongItem> = listOf()
    lateinit var binding: ItemProfileSongBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_profile_song, parent, false
        )
        return ViewHolder(binding, onItemClicked, onRejectBadgeClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        private val binding: ItemProfileSongBinding,
        private val onItemClicked: (songId: String) -> Unit,
        private val onRejectBadgeClicked: (songId: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: SongItem) {
            binding.apply {
                song = data
                root.setOnClickListener {
                    onItemClicked(data.songId)
                }
                when (data.songStatus) {
                    "PROCEEDING" -> {
                        ivSongState.setImageResource(R.drawable.ic_profile_song_pending)
                    }
                    "REJECT" -> {
                        ivSongState.apply {
                            setImageResource(R.drawable.ic_profile_song_rejected)
                            setOnClickListener { onRejectBadgeClicked(data.songId) }
                        }
                    }
                    "APPROVE" -> ivSongState.setImageResource(R.drawable.ic_profile_song_approved)
                    else -> ivSongState.visibility = View.GONE
                }
            }
        }
    }

    fun setSong(songItem: List<SongItem>) {
        this.items = songItem
        notifyDataSetChanged()
    }
}