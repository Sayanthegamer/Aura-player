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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.auraplayer.app.data.AppSettings
import com.auraplayer.app.data.ArtistEntity
import com.auraplayer.app.data.TrackEntity
import com.auraplayer.app.domain.HomeRailBuilder
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    tracks: List<TrackEntity>,
    artists: List<ArtistEntity>,
    artistScrobbleCounts: Map<String, Int>,
    settings: AppSettings,
    onTrackSelect: (TrackEntity, List<TrackEntity>, Int) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLibraryManager: () -> Unit,
    onRescanClick: () -> Unit,
    onUpdateRailSettings: (List<String>, Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showRailCustomizeSheet by remember { mutableStateOf(false) }
    var selectedMenuTrack by remember { mutableStateOf<TrackEntity?>(null) }

    val filteredTracks = remember(searchQuery, tracks) {
        if (searchQuery.isBlank()) tracks else tracks.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.artistName.contains(searchQuery, ignoreCase = true) ||
                    it.albumName.contains(searchQuery, ignoreCase = true)
        }
    }

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 4..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..22 -> "Good evening"
            else -> "Good night"
        }
    }

    val continueHero = remember(filteredTracks) {
        HomeRailBuilder.buildContinueListeningHero(filteredTracks)
    }

    val madeForYouTracks = remember(filteredTracks, continueHero, artistScrobbleCounts) {
        HomeRailBuilder.buildMadeForYouCarousel(filteredTracks, continueHero?.track, artistScrobbleCounts)
    }

    val recentlyAddedTracks = remember(filteredTracks) {
        HomeRailBuilder.buildRecentlyAdded(filteredTracks)
    }

    val mostPlayedArtistsList = remember(artists, artistScrobbleCounts) {
        artists.map { artist ->
            artist to (artistScrobbleCounts[artist.name] ?: 0)
        }.filter { it.second > 0 }.sortedByDescending { it.second }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = greeting,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Aura Music",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = onOpenLibraryManager) {
                            Icon(imageVector = Icons.Default.FolderSpecial, contentDescription = "Library Manager")
                        }
                        IconButton(onClick = { showRailCustomizeSheet = true }) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = "Customize Rails")
                        }
                        IconButton(onClick = onOpenSettings) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )

                AnimatedVisibility(visible = isSearchActive) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search songs, artists, albums...") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        },
        modifier = modifier
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (tracks.isEmpty()) {
                EmptyLibraryView(onRescanClick = onRescanClick)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 90.dp, top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    settings.homeRailOrder.forEach { railKey ->
                        if (!settings.hiddenRails.contains(railKey)) {
                            when (railKey) {
                                "CONTINUE_LISTENING" -> {
                                    if (continueHero != null) {
                                        item(key = "rail_continue") {
                                            ContinueListeningHeroCard(
                                                hero = continueHero,
                                                onPlayClick = {
                                                    onTrackSelect(continueHero.track, filteredTracks, filteredTracks.indexOf(continueHero.track).coerceAtLeast(0))
                                                }
                                            )
                                        }
                                    }
                                }
                                "MADE_FOR_YOU" -> {
                                    if (madeForYouTracks.isNotEmpty()) {
                                        item(key = "rail_made_for_you") {
                                            RailHeader(title = "Made For You")
                                            HorizontalTrackCarousel(
                                                tracks = madeForYouTracks,
                                                onTrackSelect = { track ->
                                                    onTrackSelect(track, madeForYouTracks, madeForYouTracks.indexOf(track).coerceAtLeast(0))
                                                }
                                            )
                                        }
                                    }
                                }
                                "MOST_PLAYED_ARTISTS" -> {
                                    if (mostPlayedArtistsList.isNotEmpty()) {
                                        item(key = "rail_most_played_artists") {
                                            RailHeader(title = "Most Played Artists")
                                            HorizontalArtistRow(
                                                artistsWithCounts = mostPlayedArtistsList,
                                                onArtistSelect = { artist ->
                                                    val artistTracks = filteredTracks.filter { it.artistId == artist.id }
                                                    if (artistTracks.isNotEmpty()) {
                                                        onTrackSelect(artistTracks.first(), artistTracks, 0)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                                "RECENTLY_ADDED" -> {
                                    if (recentlyAddedTracks.isNotEmpty()) {
                                        item(key = "rail_recently_added") {
                                            RailHeader(title = "Recently Added")
                                            HorizontalTrackCarousel(
                                                tracks = recentlyAddedTracks,
                                                onTrackSelect = { track ->
                                                    onTrackSelect(track, recentlyAddedTracks, recentlyAddedTracks.indexOf(track).coerceAtLeast(0))
                                                }
                                            )
                                        }
                                    }
                                }
                                "ON_REPEAT" -> {
                                    // Render all library tracks list as base fallback
                                    item(key = "rail_all_tracks_header") {
                                        RailHeader(title = "All Songs (${filteredTracks.size})")
                                    }
                                    itemsIndexed(
                                        items = filteredTracks,
                                        key = { _, item -> "track_${item.id}" }
                                    ) { index, track ->
                                        TrackListItem(
                                            track = track,
                                            onClick = { onTrackSelect(track, filteredTracks, index) },
                                            onMenuClick = { selectedMenuTrack = track },
                                            placeholderPainter = rememberVectorPainter(Icons.Default.MusicNote)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        DropdownMenu(
            expanded = selectedMenuTrack != null,
            onDismissRequest = { selectedMenuTrack = null }
        ) {
            DropdownMenuItem(
                text = { Text("Play") },
                onClick = {
                    selectedMenuTrack?.let { target ->
                        val targetIndex = tracks.indexOfFirst { it.id == target.id }
                        if (targetIndex >= 0) {
                            onTrackSelect(target, tracks, targetIndex)
                        }
                    }
                    selectedMenuTrack = null
                }
            )
        }

        if (showRailCustomizeSheet) {
            RailCustomizeBottomSheet(
                currentOrder = settings.homeRailOrder,
                currentHidden = settings.hiddenRails,
                onDismiss = { showRailCustomizeSheet = false },
                onSave = { newOrder, newHidden ->
                    onUpdateRailSettings(newOrder, newHidden)
                    showRailCustomizeSheet = false
                }
            )
        }
    }
}

@Composable
private fun RailHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )
}

@Composable
private fun ContinueListeningHeroCard(
    hero: HomeRailBuilder.HomeRail.ContinueListening,
    onPlayClick: () -> Unit
) {
    val context = LocalContext.current
    val playPainter = rememberVectorPainter(Icons.Default.PlayArrow)

    val imageRequest = remember(hero.track.albumArtUri) {
        ImageRequest.Builder(context)
            .data(hero.track.albumArtUri)
            .size(200, 200)
            .crossfade(false)
            .build()
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onPlayClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = playPainter,
                    error = playPainter,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (hero.isRecentPlayed) "Continue Listening" else "Jump Back In",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Text(
                    text = hero.track.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = hero.track.artistName,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun HorizontalTrackCarousel(
    tracks: List<TrackEntity>,
    onTrackSelect: (TrackEntity) -> Unit
) {
    val context = LocalContext.current
    val notePainter = rememberVectorPainter(Icons.Default.MusicNote)

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = tracks,
            key = { "carousel_${it.id}" }
        ) { track ->
            val imageRequest = remember(track.albumArtUri) {
                ImageRequest.Builder(context)
                    .data(track.albumArtUri)
                    .size(180, 180)
                    .crossfade(false)
                    .build()
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier
                    .width(140.dp)
                    .clickable { onTrackSelect(track) }
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = imageRequest,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            placeholder = notePainter,
                            error = notePainter,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = track.artistName,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun HorizontalArtistRow(
    artistsWithCounts: List<Pair<ArtistEntity, Int>>,
    onArtistSelect: (ArtistEntity) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = artistsWithCounts,
            key = { "artist_${it.first.id}" }
        ) { (artist, scrobbleCount) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(90.dp)
                    .clickable { onArtistSelect(artist) }
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = artist.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "$scrobbleCount plays",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TrackListItem(
    track: TrackEntity,
    onClick: () -> Unit,
    onMenuClick: () -> Unit,
    placeholderPainter: Painter
) {
    val subtitleText = remember(track.artistName, track.albumName) { "${track.artistName} • ${track.albumName}" }
    val durationText = remember(track.durationMs) { formatDuration(track.durationMs) }
    val context = LocalContext.current

    val imageRequest = remember(track.albumArtUri) {
        ImageRequest.Builder(context)
            .data(track.albumArtUri)
            .size(128, 128)
            .crossfade(false)
            .build()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageRequest,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = placeholderPainter,
                error = placeholderPainter,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

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
                text = subtitleText,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = durationText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        IconButton(onClick = onMenuClick) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
        }
    }
}

@Composable
private fun EmptyLibraryView(onRescanClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No Local Music Found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Scan your device storage to index your offline music library.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRescanClick,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Scan Storage")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RailCustomizeBottomSheet(
    currentOrder: List<String>,
    currentHidden: Set<String>,
    onDismiss: () -> Unit,
    onSave: (List<String>, Set<String>) -> Unit
) {
    var hiddenSet by remember { mutableStateOf(currentHidden) }
    val sheetState = rememberModalBottomSheetState()

    val railDisplayNames = mapOf(
        "CONTINUE_LISTENING" to "Continue Listening Hero",
        "MADE_FOR_YOU" to "Made For You Carousel",
        "MOST_PLAYED_ARTISTS" to "Most Played Artists",
        "RECENTLY_ADDED" to "Recently Added Tracks",
        "ON_REPEAT" to "All Songs / On Repeat"
    )

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
                text = "Customize Home Dashboard Rails",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            currentOrder.forEach { railKey ->
                val isVisible = !hiddenSet.contains(railKey)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = railDisplayNames[railKey] ?: railKey,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = isVisible,
                        onCheckedChange = { checked ->
                            hiddenSet = if (checked) hiddenSet - railKey else hiddenSet + railKey
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSave(currentOrder, hiddenSet) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Rail Settings")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}
