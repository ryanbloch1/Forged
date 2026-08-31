package com.example.forged.presentation.addedit

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.forged.domain.model.BodyPart
import com.example.forged.domain.model.Exercise
import com.example.forged.presentation.theme.ForgedBorder
import com.example.forged.presentation.theme.ForgedOrange
import com.example.forged.presentation.theme.ForgedOrangeDim
import com.example.forged.presentation.theme.ForgedSurface
import com.example.forged.presentation.theme.ForgedSurfaceRaised
import com.example.forged.presentation.toFormLabel

@Composable
fun AddEditScreen(
    sessionId: String? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val catalog by viewModel.catalog.collectAsState()

    LaunchedEffect(sessionId) {
        viewModel.prepare(sessionId)
    }

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
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onBack)
                    .padding(8.dp),
            )
            Text(
                text = if (uiState.isEditing) "EDIT SESSION" else "NEW SESSION",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                ),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            // Balance the back button so the title stays centered.
            Spacer(modifier = Modifier.width(38.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        FieldLabel("DATE")
        Spacer(modifier = Modifier.height(8.dp))
        DateStepper(
            label = uiState.date.toFormLabel(),
            onPrevious = { viewModel.onDateSelected(uiState.date.minusDays(1)) },
            onNext = { viewModel.onDateSelected(uiState.date.plusDays(1)) },
        )

        Spacer(modifier = Modifier.height(24.dp))

        FieldLabel("BODY PARTS")
        Spacer(modifier = Modifier.height(10.dp))
        viewModel.availableBodyParts.chunked(2).forEach { rowParts ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowParts.forEach { bodyPart ->
                    BodyPartChip(
                        bodyPart = bodyPart,
                        selected = bodyPart in uiState.selectedBodyParts,
                        onClick = { viewModel.onBodyPartToggled(bodyPart) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowParts.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel("DURATION (MIN)")
        Spacer(modifier = Modifier.height(8.dp))
        DarkField(
            value = uiState.durationMinutes,
            onValueChange = viewModel::onDurationChanged,
            placeholder = "45",
            keyboardType = KeyboardType.Number,
        )

        Spacer(modifier = Modifier.height(24.dp))

        FieldLabel("EXERCISES")
        Spacer(modifier = Modifier.height(10.dp))

        uiState.exercises.forEach { draft ->
            ExerciseCard(
                draft = draft,
                onRemove = { viewModel.removeExercise(draft.id) },
                onAddSet = { viewModel.addSet(draft.id) },
                onRemoveSet = { setId -> viewModel.removeSet(draft.id, setId) },
                onUpdateSet = { setId, reps, weight ->
                    viewModel.updateSet(draft.id, setId, reps, weight)
                },
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        AddActionRow(
            label = "+ Add exercise",
            onClick = { viewModel.showExercisePicker(true) },
        )

        Spacer(modifier = Modifier.height(28.dp))

        FieldLabel("NOTES")
        Spacer(modifier = Modifier.height(8.dp))
        DarkField(
            value = uiState.notes,
            onValueChange = viewModel::onNotesChanged,
            placeholder = "How did it feel?",
            singleLine = false,
            minLines = 3,
        )

        Spacer(modifier = Modifier.height(28.dp))
        SaveButton(onClick = { viewModel.onSave(onSuccess = onBack) })
    }

    if (uiState.showExercisePicker) {
        ExercisePickerDialog(
            exercises = catalog,
            onDismiss = { viewModel.showExercisePicker(false) },
            onSelect = viewModel::addExercise,
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun DateStepper(
    label: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ForgedSurface)
            .border(1.dp, ForgedBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "‹",
            fontSize = 22.sp,
            color = ForgedOrange,
            modifier = Modifier
                .clickable(onClick = onPrevious)
                .padding(4.dp),
        )
        Text(
            text = label,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = "›",
            fontSize = 22.sp,
            color = ForgedOrange,
            modifier = Modifier
                .clickable(onClick = onNext)
                .padding(4.dp),
        )
    }
}

@Composable
private fun BodyPartChip(
    bodyPart: BodyPart,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (selected) ForgedOrangeDim else ForgedSurface
    val border = if (selected) ForgedOrange else ForgedBorder
    val textColor = if (selected) ForgedOrange else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .border(1.dp, border, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = bodyPart.name,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        )
    }
}

@Composable
private fun DarkField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ForgedSurface)
            .border(1.dp, ForgedBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            minLines = minLines,
            cursorBrush = SolidColor(ForgedOrange),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onBackground,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ExerciseCard(
    draft: DraftExercise,
    onRemove: () -> Unit,
    onAddSet: () -> Unit,
    onRemoveSet: (String) -> Unit,
    onUpdateSet: (setId: String, reps: String?, weight: String?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ForgedSurface)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = draft.exercise.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = "Remove",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.clickable(onClick = onRemove),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "SET",
                modifier = Modifier.width(40.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "REPS",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "KG",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.width(28.dp))
        }

        draft.sets.forEachIndexed { index, set ->
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${index + 1}",
                    modifier = Modifier.width(40.dp),
                    color = ForgedOrange,
                    fontWeight = FontWeight.SemiBold,
                )
                CompactField(
                    value = set.reps,
                    onValueChange = { onUpdateSet(set.id, it.filter(Char::isDigit), null) },
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                CompactField(
                    value = set.weightKg,
                    onValueChange = { raw ->
                        onUpdateSet(set.id, null, raw.filter { it.isDigit() || it == '.' })
                    },
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "×",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .width(28.dp)
                        .clickable { onRemoveSet(set.id) }
                        .padding(start = 8.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "+ Add set",
            color = ForgedOrange,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable(onClick = onAddSet),
        )
    }
}

@Composable
private fun CompactField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ForgedSurfaceRaised)
            .border(1.dp, ForgedBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            cursorBrush = SolidColor(ForgedOrange),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onBackground,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SaveButton(onClick: () -> Unit) {
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
            text = "Save",
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )
    }
}

@Composable
private fun AddActionRow(
    label: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, ForgedBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = ForgedOrange,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun ExercisePickerDialog(
    exercises: List<Exercise>,
    onDismiss: () -> Unit,
    onSelect: (Exercise) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ForgedSurface,
        title = { Text("Add exercise") },
        text = {
            Column {
                exercises.forEachIndexed { index, exercise ->
                    Text(
                        text = exercise.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(exercise) }
                            .padding(vertical = 12.dp),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    if (index < exercises.lastIndex) {
                        HorizontalDivider(color = ForgedBorder)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
    )
}
