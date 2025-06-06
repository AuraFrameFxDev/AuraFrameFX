package com.example.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape definitions for AuraFrameFX
 * Using Material3 shapes with cyberpunk styling - more angular for a futuristic look
 */
val AppShapes = Shapes(
    // Small components like chips, small buttons
    small = RoundedCornerShape(8.dp),
    
    // Medium components like cards, dialogs
    medium = RoundedCornerShape(12.dp),
    
    // Large components like bottom sheets, side sheets
    large = RoundedCornerShape(16.dp)
)

// Additional custom shapes for specific components
val ChatBubbleIncomingShape = RoundedCornerShape(
    topStart = 4.dp,
    topEnd = 16.dp,
    bottomStart = 16.dp, 
    bottomEnd = 16.dp
)

val ChatBubbleOutgoingShape = RoundedCornerShape(
    topStart = 16.dp,
    topEnd = 4.dp, 
    bottomStart = 16.dp,
    bottomEnd = 16.dp
)

val ButtonShape = RoundedCornerShape(12.dp)
val CardShape = RoundedCornerShape(16.dp)
val InputFieldShape = RoundedCornerShape(12.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
val FloatingActionButtonShape = RoundedCornerShape(16.dp)
