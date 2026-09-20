package com.example.nexus_marvel_app.ui.screens.lab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nexus_marvel_app.ui.components.AsyncPoster
import com.example.nexus_marvel_app.ui.components.ErrorState
import com.example.nexus_marvel_app.ui.components.NexusBadge
import com.example.nexus_marvel_app.ui.components.StarField
import com.example.nexus_marvel_app.ui.screens.home.GraphNode
import com.example.nexus_marvel_app.ui.screens.home.HomeGraph
import com.example.nexus_marvel_app.ui.screens.home.HomeViewModel
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.ui.theme.Spacing
import com.example.nexus_marvel_app.ui.theme.teamColor

private const val NODE_SIZE = 56f

@Composable
fun ConstellationScreen(onBack: () -> Unit, onCharacterClick: (Int) -> Unit) {
    val vm: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val state by vm.uiState.collectAsStateWithLifecycle()

    LabScaffold(title = "Constelações", onBack = onBack) {
        Box(modifier = Modifier.fillMaxSize()) {
            StarField(modifier = Modifier.fillMaxSize())
            when {
                state.loading -> Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator(color = NexusColors.Gold)
                    Text("Mapeando o universo…", color = NexusColors.TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(top = Spacing.md))
                }
                state.error != null -> ErrorState(message = state.error!!, onRetry = vm::load)
                else -> ConstellationGraph(graph = state.graph, bottomInset = Spacing.md, onOpen = onCharacterClick)
            }
        }
    }
}

@Composable
private fun ConstellationGraph(graph: HomeGraph, bottomInset: Dp, onOpen: (Int) -> Unit) {
    var scale by remember { mutableFloatStateOf(0.72f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var selected by remember { mutableStateOf<Int?>(null) }

    val flow by rememberInfiniteTransition(label = "flow").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Restart),
        label = "flowT",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.4f, 2.5f)
                    offset += pan
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(graph.width.dp, graph.height.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                graph.edges.forEachIndexed { index, e ->
                    val a = graph.nodes[e.from]
                    val b = graph.nodes[e.to]
                    val start = Offset(a.x.dp.toPx(), a.y.dp.toPx())
                    val end = Offset(b.x.dp.toPx(), b.y.dp.toPx())
                    val color = teamColor(e.team)
                    drawLine(color = color.copy(alpha = 0.22f), start = start, end = end, strokeWidth = 1.5.dp.toPx())
                    val t = (flow + index * 0.13f) % 1f
                    val pulse = Offset(start.x + (end.x - start.x) * t, start.y + (end.y - start.y) * t)
                    val fade = 1f - kotlin.math.abs(t - 0.5f) * 2f
                    drawCircle(color = color.copy(alpha = 0.9f * fade), radius = 2.5.dp.toPx(), center = pulse)
                }
            }

            graph.nodes.forEachIndexed { index, node ->
                GraphNodeItem(
                    node = node,
                    selected = selected == index,
                    onTap = { selected = if (selected == index) null else index },
                    onDoubleTap = { onOpen(node.character.id) },
                    modifier = Modifier.offset(x = (node.x - NODE_SIZE / 2f).dp, y = (node.y - NODE_SIZE / 2f).dp),
                )
            }
        }

        val current = selected?.let { graph.nodes.getOrNull(it) }
        AnimatedVisibility(
            visible = current != null,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            current?.let { node ->
                NodeInfoCard(
                    name = node.character.name,
                    subtitle = node.team,
                    accent = teamColor(node.team),
                    onOpen = { onOpen(node.character.id) },
                    modifier = Modifier.padding(bottom = bottomInset + Spacing.md, start = Spacing.md, end = Spacing.md),
                )
            }
        }
    }
}

@Composable
private fun GraphNodeItem(
    node: GraphNode,
    selected: Boolean,
    onTap: () -> Unit,
    onDoubleTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = teamColor(node.team)
    val pulse by animateFloatAsState(targetValue = if (selected) 1.2f else 1f, label = "nodePulse")

    Box(
        modifier = modifier
            .size(NODE_SIZE.dp)
            .scale(pulse)
            .pointerInput(node.character.id) {
                detectTapGestures(onTap = { onTap() }, onDoubleTap = { onDoubleTap() })
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(NexusColors.Surface)
                .border(width = if (selected) 3.dp else 2.dp, color = color, shape = CircleShape),
        ) {
            AsyncPoster(url = node.character.imageMedium, modifier = Modifier.fillMaxSize().clip(CircleShape), contentDescription = node.character.name)
        }
    }
}

@Composable
private fun NodeInfoCard(
    name: String,
    subtitle: String,
    accent: androidx.compose.ui.graphics.Color,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(NexusColors.Surface.copy(alpha = 0.96f))
            .border(1.dp, NexusColors.BorderStrong, RoundedCornerShape(Radius.lg))
            .pointerInput(name) { detectTapGestures(onDoubleTap = { onOpen() }, onTap = { onOpen() }) }
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        NexusBadge(label = subtitle, color = accent, small = true)
        Text(name, color = NexusColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Toque para abrir o perfil • toque duplo no nó também abre", color = NexusColors.TextSecondary, fontSize = 12.sp)
    }
}
