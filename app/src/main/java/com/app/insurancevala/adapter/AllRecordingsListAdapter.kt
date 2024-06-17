package com.app.insurancevala.adapter

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.app.insurancevala.R
import com.app.insurancevala.interFase.RecyclerClickListener
import com.app.insurancevala.model.response.RecordingsModel
import com.app.insurancevala.utils.preventTwoClick
import kotlinx.android.synthetic.main.adapter_all_recordings_list.view.*

class AllRecordingsListAdapter(
    private val mContext: Context,
    private val arrayList: ArrayList<RecordingsModel>?,
    private val recyclerItemClickListener: RecyclerClickListener
) : RecyclerView.Adapter<AllRecordingsListAdapter.ViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_all_recordings_list, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(mContext, position, arrayList!![position], recyclerItemClickListener)
    }

    override fun getItemCount(): Int {
        return arrayList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(
            context: Context,
            position: Int,
            recording: RecordingsModel,
            recyclerItemClickListener: RecyclerClickListener
        ) {
            itemView.txtType.text = recording.Title

            // Handle audio play/pause icon
            if (position == currentPlayingPosition && mediaPlayer?.isPlaying == true) {
                itemView.audioPlay.setImageResource(R.drawable.ic_pause)
            } else {
                itemView.audioPlay.setImageResource(R.drawable.ic_play)
            }

            itemView.audioPlay.setOnClickListener {
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 100)
            }

            itemView.imgEdit.setOnClickListener {
                preventTwoClick(itemView.imgEdit)
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 101)
            }

            itemView.imgDelete.setOnClickListener {
                preventTwoClick(itemView.imgDelete)
                recyclerItemClickListener.onItemClickEvent(it, adapterPosition, 102)
            }
        }
    }

    fun prepareMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setOnCompletionListener {
                currentPlayingPosition = -1
                notifyDataSetChanged()
            }
        }
        // Prepare each recording
        arrayList?.forEach { recording ->
            try {
                mediaPlayer?.setDataSource(recording.RecodingFiles)
                mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updatePlayback(position: Int) {
        // Toggle playback for the clicked position
        if (position == currentPlayingPosition && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        } else {
            startAudio(arrayList?.get(position)?.RecodingFiles, position)
            currentPlayingPosition = position
        }
        notifyItemChanged(position)
    }

    private fun startAudio(url: String?, position: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(url)
            prepare()
            start()
            setOnCompletionListener {
                currentPlayingPosition = -1
                notifyItemChanged(position)
            }
        }
    }

    fun stopPlayback() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            reset()
            release()
        }
        mediaPlayer = null
    }
}
