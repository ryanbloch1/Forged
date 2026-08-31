package com.example.forged

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.forged.presentation.addedit.AddEditScreen
import com.example.forged.presentation.detail.DetailScreen
import com.example.forged.presentation.history.ForgedBottomBar
import com.example.forged.presentation.history.BottomTab
import com.example.forged.presentation.history.HistoryScreen
import com.example.forged.presentation.theme.ForgedTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForgedTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    ForgedRoot()
                }
            }
        }
    }
}

private sealed interface AppDestination {
    data object History : AppDestination
    data class AddEdit(val sessionId: String? = null) : AppDestination
    data class Detail(val sessionId: String) : AppDestination
    data object Goals : AppDestination
    data object Settings : AppDestination
}

@Composable
private fun ForgedRoot() {
    var destination by remember { mutableStateOf<AppDestination>(AppDestination.History) }

    when (val dest = destination) {
        is AppDestination.History -> {
            HistoryScreen(
                onSessionClick = { sessionId ->
                    destination = AppDestination.Detail(sessionId)
                },
                onAddClick = {
                    destination = AppDestination.AddEdit()
                },
                onGoalsClick = { destination = AppDestination.Goals },
                onSettingsClick = { destination = AppDestination.Settings },
            )
        }

        is AppDestination.AddEdit -> {
            AddEditScreen(
                sessionId = dest.sessionId,
                onBack = {
                    destination = if (dest.sessionId != null) {
                        AppDestination.Detail(dest.sessionId)
                    } else {
                        AppDestination.History
                    }
                },
            )
        }

        is AppDestination.Detail -> {
            DetailScreen(
                sessionId = dest.sessionId,
                onBack = { destination = AppDestination.History },
                onEdit = { sessionId ->
                    destination = AppDestination.AddEdit(sessionId)
                },
            )
        }

        is AppDestination.Goals -> {
            PlaceholderTab(
                title = "Goals",
                selected = BottomTab.Goals,
                onHistoryClick = { destination = AppDestination.History },
                onGoalsClick = {},
                onSettingsClick = { destination = AppDestination.Settings },
            )
        }

        is AppDestination.Settings -> {
            PlaceholderTab(
                title = "Settings",
                selected = BottomTab.Settings,
                onHistoryClick = { destination = AppDestination.History },
                onGoalsClick = { destination = AppDestination.Goals },
                onSettingsClick = {},
            )
        }
    }
}

@Composable
private fun PlaceholderTab(
    title: String,
    selected: BottomTab,
    onHistoryClick: () -> Unit,
    onGoalsClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "$title coming soon",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
        )
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            ForgedBottomBar(
                selected = selected,
                onHistoryClick = onHistoryClick,
                onGoalsClick = onGoalsClick,
                onSettingsClick = onSettingsClick,
            )
        }
    }
}
