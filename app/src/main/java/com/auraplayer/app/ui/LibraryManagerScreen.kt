package com.auraplayer.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Add

import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.auraplayer.app.data.TrackEntity
import com.auraplayer.app.domain.DuplicateDetector
import com.auraplayer.app.domain.LibraryHealthCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryManagerScreen(
    tracks: List<TrackEntity>,
    blacklistedFolders: Set<String>,
    onBack: () -> Unit,
    onAddBlacklistedFolder: (String) -> Unit,
    onRemoveBlacklistedFolder: (String) -> Unit,
    onUpdateTags: (List<Long>, String, String, Int) -> Unit,
    onDeleteTracks: (List<Long>) -> Unit,
    modifier: Modifier = Modifier
) {
    val healthSummary = remember(tracks) { LibraryHealthCalculator.calculateHealth(tracks) }
    val duplicateGroups = remember(tracks) { DuplicateDetector.findDuplicates(tracks) }

    var selectedFilter by remember { mutableStateOf<String?>(null) } // "GENRE", "ARTWORK", "BPM", "DUPLICATES", null
    var selectedTrackIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var showTagEditorSheet by remember { mutableStateOf(false) }
    var showAddFolderDialog by remember { mutableStateOf(false) }
    var newFolderPath by remember { mutableStateOf("") }

    val displayedTracks = remember(tracks, selectedFilter) {
        when (selectedFilter) {
            "GENRE" -> LibraryHealthCalculator.filterMissingGenre(tracks)
            "ARTWORK" -> LibraryHealthCalculator.filterMissingArtwork(tracks)
            "BPM" -> LibraryHealthCalculator.filterZeroBpm(tracks)
            else -> tracks
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Library Manager",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedTrackIds.isNotEmpty()) {
                        IconButton(onClick = { showTagEditorSheet = true }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Tags")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            AnimatedVisibility(visible = selectedTrackIds.isNotEmpty()) {
                BatchSelectionToolbar(
                    selectedCount = selectedTrackIds.size,
                    onEditTagsClick = { showTagEditorSheet = true },
                    onClearSelection = { selectedTrackIds = emptySet() }
                )
            }
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "health_dashboard") {
                Text(
                    text = "Metadata Health Dashboard",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HealthMetricCard(
                        title = "Missing Genre",
                        count = healthSummary.missingGenreCount,
                        icon = Icons.AutoMirrored.Filled.Label,
                        isSelected = selectedFilter == "GENRE",
                        onClick = { selectedFilter = if (selectedFilter == "GENRE") null else "GENRE" },
                        modifier = Modifier.weight(1f)
                    )
                    HealthMetricCard(
                        title = "Missing Art",
                        count = healthSummary.missingArtworkCount,
                        icon = Icons.Default.ImageNotSupported,
                        isSelected = selectedFilter == "ARTWORK",
                        onClick = { selectedFilter = if (selectedFilter == "ARTWORK") null else "ARTWORK" },
                        modifier = Modifier.weight(1f)
                    )
                    HealthMetricCard(
                        title = "Zero BPM",
                        count = healthSummary.zeroBpmCount,
                        icon = Icons.Default.Speed,
                        isSelected = selectedFilter == "BPM",
                        onClick = { selectedFilter = if (selectedFilter == "BPM") null else "BPM" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (duplicateGroups.isNotEmpty()) {
                item(key = "duplicates_header") {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Duplicates Found (${duplicateGroups.size} groups)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Multiple files detected with matching title and artist across different folders.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                items(
                    items = duplicateGroups,
                    key = { "dup_${it.title}_${it.artistName}" }
                ) { group ->
                    DuplicateGroupItem(
                        group = group,
                        onDeleteTrack = { trackId -> onDeleteTracks(listOf(trackId)) }
                    )
                }
            }

            item(key = "blacklist_section") {
                Text(
                    text = "Excluded Storage Folders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (blacklistedFolders.isEmpty()) {
                            Text(
                                text = "No folders excluded. MediaScanner scans all audio locations.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            blacklistedFolders.forEach { folder ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FolderOff,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = folder,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    IconButton(onClick = { onRemoveBlacklistedFolder(folder) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove")
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(
                                onClick = { showAddFolderDialog = !showAddFolderDialog },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Excluded Folder")
                            }
                        }

                        if (showAddFolderDialog) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = newFolderPath,
                                onValueChange = { newFolderPath = it },
                                label = { Text("Folder path keyword (e.g. Podcasts)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (newFolderPath.isNotBlank()) {
                                        onAddBlacklistedFolder(newFolderPath.trim())
                                        newFolderPath = ""
                                        showAddFolderDialog = false
                                    }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Save Keyword")
                            }
                        }
                    }
                }
            }

            item(key = "track_list_header") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (selectedFilter != null) "Filtered Tracks (${displayedTracks.size})" else "All Library Tracks (${tracks.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )

                    if (displayedTracks.isNotEmpty()) {
                        val allSelected = selectedTrackIds.size == displayedTracks.size
                        IconButton(onClick = {
                            selectedTrackIds = if (allSelected) emptySet() else displayedTracks.map { it.id }.toSet()
                        }) {
                            Icon(
                                imageVector = if (allSelected) Icons.Default.CheckCircle else Icons.Default.Check,
                                contentDescription = "Select All"
                            )
                        }
                    }
                }
            }

            items(
                items = displayedTracks,
                key = { it.id }
            ) { track ->
                val isSelected = selectedTrackIds.contains(track.id)
                TrackManagerListItem(
                    track = track,
                    isSelected = isSelected,
                    onToggleSelect = {
                        selectedTrackIds = if (isSelected) selectedTrackIds - track.id else selectedTrackIds + track.id
                    }
                )
            }
        }

        if (showTagEditorSheet && selectedTrackIds.isNotEmpty()) {
            TagEditorBottomSheet(
                selectedTrackCount = selectedTrackIds.size,
                onDismiss = { showTagEditorSheet = false },
                onSave = { genre, moodTags, bpm ->
                    onUpdateTags(selectedTrackIds.toList(), genre, moodTags, bpm)
                    selectedTrackIds = emptySet()
                    showTagEditorSheet = false
                }
            )
        }
    }
}

@Composable
private fun HealthMetricCard(
    title: String,
    count: Int,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$count",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DuplicateGroupItem(
    group: DuplicateDetector.DuplicateGroup,
    onDeleteTrack: (Long) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${group.title} • ${group.artistName}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            group.tracks.forEach { track ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.filePath,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onDeleteTrack(track.id) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete track")
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackManagerListItem(
    track: TrackEntity,
    isSelected: Boolean,
    onToggleSelect: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleSelect() }
            .padding(vertical = 6.dp)
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggleSelect() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "${track.artistName} • Genre: ${track.genre.ifBlank { "None" }} • BPM: ${track.bpm}",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BatchSelectionToolbar(
    selectedCount: Int,
    onEditTagsClick: () -> Unit,
    onClearSelection: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "$selectedCount selected",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = onEditTagsClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Edit Tags")
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onClearSelection) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Clear selection")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagEditorBottomSheet(
    selectedTrackCount: Int,
    onDismiss: () -> Unit,
    onSave: (String, String, Int) -> Unit
) {
    var genreText by remember { mutableStateOf("") }
    var moodTagsText by remember { mutableStateOf("") }
    var bpmText by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Bulk Tag Editor ($selectedTrackCount tracks)",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = genreText,
                onValueChange = { genreText = it },
                label = { Text("Genre (e.g. Synthwave, Rock)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = moodTagsText,
                onValueChange = { moodTagsText = it },
                label = { Text("Mood Tags (e.g. energetic, night-drive)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = bpmText,
                onValueChange = { bpmText = it },
                label = { Text("BPM (e.g. 128)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val parsedBpm = bpmText.toIntOrNull() ?: 0
                    onSave(genreText.trim(), moodTagsText.trim(), parsedBpm)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Tags")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
