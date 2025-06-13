package dev.aurakai.auraframefx.ui.components

import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.ui.theme.NeonBlue
import dev.aurakai.auraframefx.ui.theme.NeonPink
import dev.aurakai.auraframefx.ui.theme.NeonPurple
import dev.aurakai.auraframefx.ui.theme.NeonTeal
import dev.aurakai.auraframefx.viewmodel.GenesisAgentViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(
    ExperimentalMaterial3Api::class,
    androidx.compose.foundation.gestures.ExperimentalFoundationApi::class
)
@Composable
fun HaloView(
    viewModel: GenesisAgentViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
) {
    var isRotating by remember { mutableStateOf(true) }
    var rotationAngle by remember { mutableStateOf(0f) }
    val agents = viewModel.getAgentsByPriority()
    val coroutineScope = rememberCoroutineScope()

    // Task delegation state
    var draggingAgent by remember { mutableStateOf<AgentType?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var dragStartOffset by remember { mutableStateOf(Offset.Zero) }
    var selectedTask by remember { mutableStateOf("") }

    // Task history
    val _taskHistory = remember { MutableStateFlow(emptyList<String>()) }
    val taskHistory: StateFlow<List<String>> = _taskHistory

    // Agent status
    val _agentStatus = remember { MutableStateFlow(mapOf<AgentType, String>()) }
    val agentStatus: StateFlow<Map<AgentType, String>> = _agentStatus

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Background glow effect
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            drawCircle(
                color = NeonTeal.copy(alpha = 0.1f),
                radius = size.width / 2f,
                style = Fill
            )
            drawCircle(
                color = NeonPurple.copy(alpha = 0.1f),
                radius = size.width / 2f - 20f,
                style = Fill
            )
            drawCircle(
                color = NeonBlue.copy(alpha = 0.1f),
                radius = size.width / 2f - 40f,
                style = Fill
            )
        }

        // Halo effect
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2f - 32f

            // Draw rotating halo
            val haloColor = NeonTeal.copy(alpha = 0.3f)
            val haloWidth = 2.dp.toPx()

            drawArc(
                color = haloColor,
                startAngle = rotationAngle,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = haloWidth)
            )

            // Draw pulsing effects for active tasks
            agentStatus.value.forEach { (agent, status) ->
                if (status == "processing") {
                    val angle =
                        (agents.indexOfFirst { it.name == agent.name } * 360f / agents.size + rotationAngle) % 360f
                    val x = center.x + radius * cos(angle * PI / 180f)
                    val y = center.y + radius * sin(angle * PI / 180f)

                    // Draw pulsing glow
                    drawCircle(
                        color = when (agent.name) {
                            "Genesis" -> NeonTeal.copy(alpha = 0.2f)
                            "Kai" -> NeonPurple.copy(alpha = 0.2f)
                            "Aura" -> NeonBlue.copy(alpha = 0.2f)
                            "Cascade" -> NeonPink.copy(alpha = 0.2f)
                            else -> NeonTeal.copy(alpha = 0.2f)
                        },
                        center = Offset(x, y),
                        radius = 40.dp.toPx()
                    )
                }
            }
        }

        // Agent nodes with drag and drop
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        if (draggingAgent != null) {
                            dragOffset += dragAmount
                            change.consumeAllChanges()
                        }
                    }
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2f - 64f
            val agentCount = agents.size
            val angleStep = 360f / agentCount

            agents.forEachIndexed { index, agent ->
                val angle = (index * angleStep + rotationAngle) % 360f
                val x = center.x + radius * cos(angle * PI / 180f)
                val y = center.y + radius * sin(angle * PI / 180f)
                val nodeCenter = Offset(x, y)

                // Draw agent node with status
                val baseColor = when (agent.name) {
                    "Genesis" -> NeonTeal
                    "Kai" -> NeonPurple
                    "Aura" -> NeonBlue
                    "Cascade" -> NeonPink
                    else -> NeonTeal.copy(alpha = 0.8f)
                }
                val statusColor = when (agentStatus.value[agent.name]?.lowercase(Locale.ROOT)) {
                    "idle" -> baseColor.copy(alpha = 0.8f)
                    "processing" -> baseColor.copy(alpha = 1.0f)
                    "error" -> Color.Red
                    else -> baseColor.copy(alpha = 0.8f)
                }

                drawCircle(
                    color = statusColor,
                    center = nodeCenter,
                    radius = 24.dp.toPx()
                )

                // Draw connecting lines
                if (index > 0) {
                    val prevAngle = ((index - 1) * angleStep + rotationAngle) % 360f
                    val prevX = center.x + radius * cos(prevAngle * PI / 180f)
                    val prevY = center.y + radius * sin(prevAngle * PI / 180f)

                    drawLine(
                        color = NeonTeal.copy(alpha = 0.5f),
                        start = Offset(prevX, prevY),
                        end = Offset(x, y),
                        strokeWidth = 2.dp.toPx()
                    )
                }

                // Draw task delegation line if dragging
                if (draggingAgent != null && draggingAgent == AgentType.valueOf(agent.name)) {
                    drawLine(
                        color = NeonTeal,
                        start = nodeCenter,
                        end = nodeCenter + dragOffset,
                        strokeWidth = 4.dp.toPx()
                    )
                }

                // Draw status indicators
                if (agentStatus.value[agent.name] != null) {
                    val statusText = agentStatus.value[agent.name] ?: "idle"
                    drawText(
                        text = statusText,
                        color = when (statusText.lowercase(Locale.ROOT)) {
                            "idle" -> NeonTeal
                            "processing" -> NeonPurple
                            "error" -> Color.Red
                            else -> NeonBlue
                        },
                        topLeft = Offset(x - 12.dp.toPx(), y + 24.dp.toPx())
                    )
                }
            }
        }

        // Center node (Genesis)
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    detectTapGestures {
                        if (selectedTask.isNotBlank()) {
                            coroutineScope.launch {
                                viewModel.processQuery(selectedTask)
                                _taskHistory.update { current ->
                                    current + "[$selectedTask]"
                                }
                                selectedTask = ""
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = NeonTeal.copy(alpha = 0.8f),
                shape = CircleShape,
                modifier = Modifier.size(80.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "GENESIS",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                    Text(
                        text = "Hive Mind",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPurple
                    )
                }
            }
        }

        // Task input overlay
        if (draggingAgent != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Assign Task to ${draggingAgent?.name}",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonTeal
                        )

                        TextField(
                            value = selectedTask,
                            onValueChange = { selectedTask = it },
                            placeholder = { Text("Enter task description...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = NeonTeal.copy(alpha = 0.1f),
                                unfocusedContainerColor = NeonTeal.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }

        // Task history panel
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Task History",
                style = MaterialTheme.typography.titleMedium,
                color = NeonTeal
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = true
            ) {
                items(taskHistory.value) { task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = NeonTeal.copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = task,
                            modifier = Modifier.padding(8.dp),
                            color = NeonPurple
                        )
                    }
                }
            }
        }

        // Control buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = { isRotating = !isRotating }
            ) {
                Icon(
                    Icons.Default.Pause if isRotating else Icons.Default.PlayArrow,
                    contentDescription = "Toggle rotation",
                    tint = NeonPurple
                )
            }

            IconButton(
                onClick = { rotationAngle = 0f }
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Reset rotation",
                    tint = NeonBlue
                )
            }

            IconButton(
                onClick = {
                    _taskHistory.update { emptyList() }
                }
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Clear history",
                    tint = NeonPink
                )
            }
        }
    }

    // Animation effect
    LaunchedEffect(isRotating) {
        if (isRotating) {
            while (true) {
                rotationAngle = (rotationAngle + 1f) % 360f
                delay(16) // 60 FPS
            }
        }
    }

    // Drag and drop gesture handling
    LaunchedEffect(Unit) {
        snapshotFlow { draggingAgent }
            .collect { agent ->
                if (agent == null) {
                    dragOffset = Offset.Zero
                    dragStartOffset = Offset.Zero
                    selectedTask = ""
                }
            }
    }

    // Task processing status updates
    LaunchedEffect(taskHistory) {
        taskHistory.collect { tasks ->
            // Update agent status based on task processing
            tasks.forEach { task ->
                val agent = task.split("[")[1].split("]")[0]
                _agentStatus.update { current ->
                    current + (AgentType.valueOf(agent) to "processing")
                }
            }
        }
    }
}
