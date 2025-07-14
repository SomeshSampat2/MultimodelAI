package com.example.multimodelai.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // Commented out tab navigation - focusing only on video analysis
    // var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    // val tabs = listOf(
    //     TabItem("Text", Icons.Default.TextFields),
    //     TabItem("Image", Icons.Default.Image),
    //     TabItem("Audio", Icons.Default.AudioFile),
    //     TabItem("Video", Icons.Default.VideoFile),
    //     TabItem("Document", Icons.Default.Description)
    // )
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar - Updated for Video Analysis focus
        TopAppBar(
            title = {
                Text(
                    text = "🎥 Video AI Analysis",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        )
        
        // Commented out Tab Row - no longer needed
        // TabRow(
        //     selectedTabIndex = selectedTabIndex,
        //     containerColor = MaterialTheme.colorScheme.surface,
        //     contentColor = MaterialTheme.colorScheme.onSurface
        // ) {
        //     tabs.forEachIndexed { index, tab ->
        //         Tab(
        //             selected = selectedTabIndex == index,
        //             onClick = { selectedTabIndex = index },
        //             text = {
        //                 Text(
        //                     text = tab.title,
        //                     fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
        //                 )
        //             },
        //             icon = {
        //                 Icon(
        //                     imageVector = tab.icon,
        //                     contentDescription = tab.title
        //                 )
        //             }
        //         )
        //     }
        // }
        
        // Content - Now only showing VideoScreen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Only Video Analysis Screen
            VideoScreen()
            
            // Commented out other screens
            // when (selectedTabIndex) {
            //     0 -> TextScreen()
            //     1 -> ImageScreen()
            //     2 -> AudioScreen()
            //     3 -> VideoScreen()
            //     4 -> DocumentScreen()
            // }
        }
    }
}

// Keeping data class for potential future use
data class TabItem(
    val title: String,
    val icon: ImageVector
) 