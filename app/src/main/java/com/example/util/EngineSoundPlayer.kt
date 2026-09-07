package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

object EngineSoundPlayer {

    fun playEngineRevSound() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val sampleRate = 22050
                val durationSeconds = 0.75
                val numSamples = (durationSeconds * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                // Synthesize an energetic V8 engine rev sound
                for (i in 0 until numSamples) {
                    val progress = i.toDouble() / numSamples

                    // Frequency envelope: Idle rumble -> Quick accel rev up -> Gentle coasting down
                    val pitchHz = if (progress < 0.45) {
                        90.0 + (progress / 0.45) * 280.0 // 90Hz to 370Hz
                    } else {
                        370.0 - ((progress - 0.45) / 0.55) * 200.0 // 370Hz to 170Hz
                    }

                    val t = i.toDouble() / sampleRate
                    val phase = 2 * PI * pitchHz * t

                    // Rich engine harmonics
                    val fundamental = sin(phase)
                    val secondHarmonic = 0.75 * sin(phase * 2.0)
                    val thirdHarmonic = 0.4 * sin(phase * 3.0)
                    val subGrowl = 0.5 * sin(phase * 0.5)

                    // Amplitude envelope
                    val envelope = when {
                        progress < 0.05 -> progress / 0.05
                        progress > 0.65 -> (1.0 - progress) / 0.35
                        else -> 1.0
                    }

                    val rawSample = (fundamental + secondHarmonic + thirdHarmonic + subGrowl) * envelope * 0.35
                    val clamped = rawSample.coerceIn(-1.0, 1.0)
                    buffer[i] = (clamped * Short.MAX_VALUE).toInt().toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()

                kotlinx.coroutines.delay((durationSeconds * 1000).toLong() + 150)
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
