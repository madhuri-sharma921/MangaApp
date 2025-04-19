package com.example.mangaverseapp.ui

import com.example.mangaverseapp.R
import androidx.compose.runtime.getValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mangaverseapp.domain.model.MangaModel
import com.example.mangaverseapp.ui.MangaListViewModel

@Composable
fun MangaListScreen(
    viewModel: MangaListViewModel = hiltViewModel(),
    navigateToDetail: (String) -> Unit
) {
    val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsState()
    val hasCachedData by viewModel.hasCachedData.collectAsState()
    val mangaItems = viewModel.mangaListFlow.collectAsLazyPagingItems()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (!isNetworkAvailable && hasCachedData) {
                OfflineBanner()
            }

            MangaGrid(
                mangaItems = mangaItems,
                navigateToDetail = navigateToDetail
            )
        }

        // Show loading for initial load
        mangaItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    LoadingScreen(modifier = Modifier.align(Alignment.Center))
                }
                loadState.refresh is LoadState.Error -> {
                    val error = loadState.refresh as LoadState.Error
                    ErrorScreen(
                        message = error.error.localizedMessage ?: "Unknown error occurred",
                        modifier = Modifier.align(Alignment.Center),
                        onRetry = { retry() }
                    )
                }
            }
        }
    }
}

@Composable
fun MangaGrid(
    mangaItems: LazyPagingItems<MangaModel>,
    navigateToDetail: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(mangaItems.itemCount) { index ->
            mangaItems[index]?.let { manga ->
                MangaItem(
                    manga = manga,
                    onItemClick = { navigateToDetail(manga.id) }
                )
            }
        }


        item(span = { GridItemSpan(maxLineSpan) }) {
            when {
                mangaItems.loadState.append is LoadState.Loading -> {
                    LoadingItem(modifier = Modifier.fillMaxWidth())
                }
                mangaItems.loadState.append is LoadState.Error -> {
                    val error = mangaItems.loadState.append as LoadState.Error
                    ErrorItem(
                        message = error.error.localizedMessage ?: "Unknown error occurred",
                        onRetry = { mangaItems.retry() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun MangaItem(
    manga: MangaModel,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(manga.thumb)
                    .crossfade(true)
                    .build(),
                contentDescription = manga.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                error = painterResource(id = R.drawable.placeholder_image),
                placeholder = painterResource(id = R.drawable.placeholder_image)
            )

            Text(
                text = manga.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun OfflineBanner() {
    Surface(
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "You are offline. Showing cached data.",
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun LoadingItem(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(8.dp)
        )
        Button(onClick = onRetry) {
            Text(text = "Retry")
        }
    }
}

@Composable
fun ErrorItem(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )
        Button(onClick = onRetry) {
            Text(text = "Retry")
        }
    }
}