package com.example.forged.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.forged.domain.model.ExerciseEntry
import com.example.forged.domain.model.Session
import com.example.forged.presentation.theme.ForgedBorder
import com.example.forged.presentation.theme.ForgedDelete
import com.example.forged.presentation.theme.ForgedOrange
import com.example.forged.presentation.theme.ForgedOrangeDim
import com.example.forged.presentation.theme.ForgedSurface
import com.example.forged.presentation.toFormLabel
import java.util.Locale

@Composable
fun DetailScreen(
    sessionId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(sessionId) {
        viewModel.load(sessionId)
    }

    when (val state = uiState) {
        is DetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ForgedOrange)
            }
        }

        is DetailUiState.Missing -> {
            LaunchedEffect(Unit) { onBack() }
        }

        is DetailUiState.Ready -> {
            DetailContent(
                session = state.session,
                onBack = onBack,
                onEdit = { onEdit(state.session.id) },
                onDelete = { viewModel.delete(onDone = onBack) },
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun DetailContent(
    session: Session,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalSets = session.exercises.sumOf { it.sets.size }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 32.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "←",
                fontSize = 22.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onBack)
                    .padding(8.dp),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = session.date.toFormLabel().uppercase(Locale.UK),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                ),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Edit",
                color = ForgedOrange,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onEdit)
                    .padding(8.dp),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StatCard(
                value = "${session.exercises.size}",
                label = "exercises",
                modifier = Modifier.weight(1f),
            )
            StatCard(
                value = "${session.durationMinutes}",
                label = "min",
                modifier = Modifier.weight(1f),
            )
            StatCard(
                value = "$totalSets",
                label = "sets",
                modifier = Modifier.weight(1f),
            )
        }

        if (session.bodyParts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            session.bodyParts.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    row.forEach { part ->
                        MuscleTag(label = part)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        session.exercises.forEach { entry ->
            ExerciseDetailCard(entry = entry)
            Spacer(modifier = Modifier.height(12.dp))
        }

        session.notes?.takeIf { it.isNotBlank() }?.let { notes ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "NOTES",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notes,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        PrimaryButton(label = "Edit session", onClick = onEdit)
        Spacer(modifier = Modifier.height(12.dp))
        DestructiveButton(label = "Delete", onClick = onDelete)
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(ForgedSurface)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = ForgedOrange,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MuscleTag(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ForgedOrangeDim)
            .border(1.dp, ForgedOrange.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            color = ForgedOrange,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        )
    }
}

@Composable
private fun ExerciseDetailCard(entry: ExerciseEntry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ForgedSurface)
            .padding(16.dp),
    ) {
        Text(
            text = entry.exercise.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(modifier = Modifier.height(10.dp))
        entry.sets.forEach { set ->
            val weight = set.weightKg?.let { String.format(Locale.US, "%.0f kg", it) } ?: "—"
            Text(
                text = "Set ${set.setNumber}  ·  ${set.reps} reps  ·  $weight",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 2.dp),
            )
        }
    }
}

@Composable
private fun PrimaryButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ForgedOrange)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun DestructiveButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, ForgedDelete, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = ForgedDelete,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
