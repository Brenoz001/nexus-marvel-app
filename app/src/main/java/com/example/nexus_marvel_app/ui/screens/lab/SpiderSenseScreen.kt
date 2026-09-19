package com.example.nexus_marvel_app.ui.screens.lab

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nexus_marvel_app.domain.model.Character
import com.example.nexus_marvel_app.ui.components.ErrorState
import com.example.nexus_marvel_app.ui.components.NexusBadge
import com.example.nexus_marvel_app.ui.theme.BebasNeue
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.ui.theme.Spacing
import kotlin.math.min
import kotlin.math.sqrt
import kotlinx.coroutines.launch

@Composable
fun SpiderSenseScreen(onBack: () -> Unit, onCharacterClick: (Int) -> Unit) {
    val vm: SpiderSenseViewModel = viewModel(factory = SpiderSenseViewModel.Factory)
    val state by vm.uiState.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val flash = remember { Animatable(0f) }

    val detect: () -> Unit = {
        if (state.pool.isNotEmpty()) {
            vm.detect()
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            scope.launch {
                flash.snapTo(0.55f)
                flash.animateTo(0f, animationSpec = tween(500))
            }
        }
    }
    val latestDetect = rememberUpdatedState(detect)

    // Shake detection via accelerometer.
    DisposableEffect(Unit) {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sm?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        var lastShake = 0L
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val gx = event.values[0] / SensorManager.GRAVITY_EARTH
                val gy = event.values[1] / SensorManager.GRAVITY_EARTH
                val gz = event.values[2] / SensorManager.GRAVITY_EARTH
                val gForce = sqrt(gx * gx + gy * gy + gz * gz)
                if (gForce > 2.6f) {
                    val now = System.currentTimeMillis()
                    if (now - lastShake > 1200) {
                        lastShake = now
                        latestDetect.value()
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        if (accelerometer != null) {
            sm.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        }
        onDispose { sm?.unregisterListener(listener) }
    }

    LabScaffold(title = "Sentido Aranha", onBack = onBack) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (state.revealed == null) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = detect,
                        )
                    } else Modifier
                ),
        ) {
            RadarWaves(modifier = Modifier.fillMaxSize())

            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NexusColors.Red)
                }
                state.error != null -> ErrorState(message = state.error!!, onRetry = vm::load)
                state.revealed == null -> Column(
                    modifier = Modifier.fillMaxSize().padding(Spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("CHACOALHE", fontFamily = BebasNeue, fontSize = 46.sp, letterSpacing = 2.sp, color = NexusColors.RedLight, textAlign = TextAlign.Center)
                    Text("PARA DETECTAR", fontFamily = BebasNeue, fontSize = 28.sp, letterSpacing = 3.sp, color = NexusColors.TextPrimary, textAlign = TextAlign.Center)
                    Text(
                        "(ou toque na tela — útil no emulador)",
                        color = NexusColors.TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = Spacing.md),
                    )
                }
                else -> Reveal(character = state.revealed!!, onOpen = { onCharacterClick(state.revealed!!.id) }, onAgain = detect)
            }

            // red flash on detect
            Box(modifier = Modifier.fillMaxSize().background(NexusColors.Red.copy(alpha = flash.value.coerceIn(0f, 1f))))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Reveal(character: Character, onOpen: () -> Unit, onAgain: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        NexusBadge(label = "Ameaça detectada", color = NexusColors.Red)
        LabAvatar(url = character.imageOriginal ?: character.imageMedium, size = 160.dp, ringColor = NexusColors.RedLight, ringWidth = 3.dp, contentDescription = character.name)
        Text(character.name.uppercase(), fontFamily = BebasNeue, fontSize = 36.sp, letterSpacing = 1.sp, color = NexusColors.TextPrimary, textAlign = TextAlign.Center)
        if (character.powers.isNotEmpty()) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally), verticalArrangement = Arrangement.spacedBy(Spacing.xs), modifier = Modifier.fillMaxWidth()) {
                character.powers.take(8).forEach { NexusBadge(label = it.name, color = NexusColors.Gold, small = true) }
            }
        }
        character.deck?.let {
            Text(it, color = NexusColors.TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center, maxLines = 4, overflow = TextOverflow.Ellipsis)
        }
        Box(
            modifier = Modifier
                .padding(top = Spacing.md)
                .clip(RoundedCornerShape(Radius.full))
                .background(NexusColors.Red)
                .clickable(onClick = onOpen)
                .padding(horizontal = Spacing.xl, vertical = Spacing.md),
        ) {
            Text("VER PERFIL COMPLETO", color = NexusColors.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 0.5.sp)
        }
        Text("Detectar outra ameaça", color = NexusColors.TextSecondary, fontSize = 13.sp, modifier = Modifier.clickable(onClick = onAgain).padding(Spacing.sm))
    }
}

@Composable
private fun RadarWaves(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "radar")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(2600, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "wave",
    )
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxR = min(size.width, size.height) / 2f * 0.92f
        for (k in 0 until 3) {
            val frac = (phase + k / 3f) % 1f
            drawCircle(
                color = NexusColors.RedLight.copy(alpha = (1f - frac) * 0.45f),
                radius = maxR * frac,
                center = center,
                style = Stroke(width = 2.dp.toPx()),
            )
        }
    }
}
