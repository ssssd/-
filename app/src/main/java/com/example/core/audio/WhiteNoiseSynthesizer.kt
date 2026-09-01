package com.example.core.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.sin

enum class WhiteNoiseType(val titleKey: String, val icon: String) {
    RAIN("noiseRain", "water_drop"),
    FOREST("noiseForest", "park"),
    CAFE("noiseCafe", "local_cafe"),
    OCEAN_WAVES("noiseWaves", "waves"),
    FIREPLACE("noiseFireplace", "local_fire_department")
}

class WhiteNoiseSynthesizer {
    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private var currentType: WhiteNoiseType? = null
    private var currentVolume: Float = 0.5f
    private var isPlaying: Boolean = false

    fun startNoise(type: WhiteNoiseType, volume: Float = 0.5f, scope: CoroutineScope) {
        stopNoise()
        currentType = type
        currentVolume = volume
        isPlaying = true

        val sampleRate = 44100
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()

            synthJob = scope.launch(Dispatchers.Default) {
                val buffer = ShortArray(bufferSize)
                val random = Random()
                var lastOut = 0.0
                var phase = 0.0

                while (isActive && isPlaying) {
                    for (i in buffer.indices) {
                        val white = random.nextDouble() * 2.0 - 1.0
                        var sample = 0.0

                        when (type) {
                            WhiteNoiseType.RAIN -> {
                                // Pink / Brown noise filter
                                lastOut = (lastOut * 0.95) + (white * 0.05)
                                sample = lastOut * 1.5
                            }
                            WhiteNoiseType.OCEAN_WAVES -> {
                                // Low frequency wave modulation (0.15 Hz)
                                phase += 2.0 * Math.PI * 0.15 / sampleRate
                                val modulation = 0.3 + 0.7 * (0.5 * (1.0 + sin(phase)))
                                lastOut = (lastOut * 0.96) + (white * 0.04)
                                sample = lastOut * modulation * 2.0
                            }
                            WhiteNoiseType.FOREST -> {
                                // Gentle wind + filtered rustling
                                phase += 2.0 * Math.PI * 0.08 / sampleRate
                                val wind = 0.4 + 0.6 * (0.5 * (1.0 + sin(phase)))
                                lastOut = (lastOut * 0.92) + (white * 0.08)
                                sample = lastOut * wind
                            }
                            WhiteNoiseType.CAFE -> {
                                // Low rumble
                                lastOut = (lastOut * 0.98) + (white * 0.02)
                                sample = lastOut * 2.5
                            }
                            WhiteNoiseType.FIREPLACE -> {
                                // Crackle impulses + warm noise
                                lastOut = (lastOut * 0.94) + (white * 0.06)
                                val crackle = if (random.nextDouble() < 0.001) (random.nextDouble() * 2.0 - 1.0) * 0.8 else 0.0
                                sample = lastOut + crackle
                            }
                        }

                        val finalVal = (sample * currentVolume * Short.MAX_VALUE * 0.5).coerceIn(
                            Short.MIN_VALUE.toDouble(),
                            Short.MAX_VALUE.toDouble()
                        )
                        buffer[i] = finalVal.toInt().toShort()
                    }
                    audioTrack?.write(buffer, 0, buffer.size)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0f, 1f)
    }

    fun stopNoise() {
        isPlaying = false
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // Ignore
        }
        audioTrack = null
        currentType = null
    }

    fun getCurrentType(): WhiteNoiseType? = if (isPlaying) currentType else null
    fun isPlayingNoise(): Boolean = isPlaying
}
