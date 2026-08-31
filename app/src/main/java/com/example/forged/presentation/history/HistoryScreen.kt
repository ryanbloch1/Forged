package com.example.forged.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.forged.domain.model.Session
import com.example.forged.presentation.theme.ForgedOrange
import com.example.forged.presentation.theme.ForgedStatusGreen
import com.example.forged.presentation.theme.ForgedSurface
import com.example.forged.presentation.toHistoryLabel

@Composable
fun HistoryScreen(
    onSessionClick: (String) -> Unit,
    onAddClick: () -> Unit,
    onGoalsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = ForgedOrange,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
            ) {
                Text("+", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
        },
        bottomBar = {
            ForgedBottomBar(
                selected = BottomTab.History,
                onHistoryClick = {},
                onGoalsClick = onGoalsClick,
                onSettingsClick = onSettingsClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
        ) {
            Text(
                text = "FORGED",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
            )

            when (val state = uiState) {
                is HistoryUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ForgedOrange)
                    }
                }

                is HistoryUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }

                is HistoryUiState.Success -> {
                    if (state.sessions.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No sessions yet.\nTap + to add one.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(state.sessions, key = { it.id }) { session ->
                                SessionCard(
                                    session = session,
                                    onClick = { onSessionClick(session.id) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionCard(
    session: Session,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ForgedSurface)
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = session.date.toHistoryLabel(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(ForgedStatusGreen),
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${session.exercises.size} exercises · ${session.durationMinutes} min",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (session.bodyParts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = session.bodyParts.joinToString(", "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

enum class BottomTab { History, Goals, Settings }

@Composable
fun ForgedBottomBar(
    selected: BottomTab,
    onHistoryClick: () -> Unit,
    onGoalsClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
            .padding(horizontal = 28.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BottomItem(label = "◷", selected = selected == BottomTab.History, onClick = onHistoryClick)
        BottomItem(label = "◎", selected = selected == BottomTab.Goals, onClick = onGoalsClick)
        BottomItem(label = "⚙", selected = selected == BottomTab.Settings, onClick = onSettingsClick)
    }
}

@Composable
private fun BottomItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = label,
        fontSize = 22.sp,
        color = if (selected) ForgedOrange else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(10.dp),
    )
}
