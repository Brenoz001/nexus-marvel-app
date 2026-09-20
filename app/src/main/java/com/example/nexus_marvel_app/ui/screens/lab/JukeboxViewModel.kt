package com.example.nexus_marvel_app.ui.screens.lab

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nexus_marvel_app.data.repository.MusicRepository
import com.example.nexus_marvel_app.di.Graph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Plays 30-second song previews in-app; tracks which key is playing/loading. */
class JukeboxViewModel(private val music: MusicRepository) : ViewModel() {

    private var player: MediaPlayer? = null

    private val _playing = MutableStateFlow<String?>(null)
    val playing = _playing.asStateFlow()

    private val _loading = MutableStateFlow<String?>(null)
    val loading = _loading.asStateFlow()

    /**
     * Toggle playback for [key]. If no preview is found, calls [onNoPreview]
     * (the caller can then open YouTube as a fallback).
     */
    fun toggle(key: String, track: String, artist: String, onNoPreview: () -> Unit) {
        if (_playing.value == key) {
            stop()
            return
        }
        viewModelScope.launch {
            _loading.value = key
            val url = music.previewUrl(track, artist)
            _loading.value = null
            if (url == null) {
                onNoPreview()
            } else {
                play(key, url)
            }
        }
    }

    private fun play(key: String, url: String) {
        release()
        player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setOnPreparedListener { it.start(); _playing.value = key }
            setOnCompletionListener { _playing.value = null; release() }
            setOnErrorListener { _, _, _ -> _playing.value = null; true }
            runCatching {
                setDataSource(url)
                prepareAsync()
            }.onFailure { _playing.value = null }
        }
    }

    fun stop() {
        _playing.value = null
        release()
    }

    private fun release() {
        player?.runCatching { reset(); release() }
        player = null
    }

    override fun onCleared() {
        release()
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { JukeboxViewModel(Graph.musicRepository) }
        }
    }
}
