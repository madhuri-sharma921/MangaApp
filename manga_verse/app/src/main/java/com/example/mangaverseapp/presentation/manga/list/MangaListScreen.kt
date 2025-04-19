//package com.example.mangaverseapp.presentation.manga.list
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import androidx.paging.LoadState
//import androidx.paging.compose.LazyPagingItems
//import androidx.paging.compose.collectAsLazyPagingItems
//import coil.compose.AsyncImage
//import com.example.mangaverseapp.data.local.entity.MangaEntity
//import com.example.mangaverseapp.domain.model.Manga
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MangaListScreen(
//    navController: NavController,
//    viewModel: MangaListViewModel = hiltViewModel()
//) {
//    val mangaItems = viewModel.mangaListFlow.collectAsLazyPagingItems()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("MangaVerse") },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                )
//            )
//        }
//    ) { paddingValues ->
//        MangaList(
//            navController = navController,
//            viewModel = viewModel,
//            mangaItems = mangaItems,
//            modifier = Modifier.padding(paddingValues)
//        )
//    }
//}
//
//@Composable
//fun MangaList(
//    navController: NavController,
//    viewModel: MangaListViewModel,
//    mangaItems: LazyPagingItems<Manga>,
//    modifier: Modifier = Modifier
//) {
//    Box(modifier = modifier.fillMaxSize()) {
//        LazyColumn(
//            modifier = Modifier.fillMaxSize(),
//            contentPadding = PaddingValues(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            items(mangaItems.itemCount) { index ->
//                mangaItems[index]?.let { manga ->
//                    MangaItem(
//                        manga = manga,
//                        onClick = {
//                            viewModel.selectManga(manga.id)
//                            navController.navigate("manga_details/${manga.id}")
//                        }
//                    )
//                }
//            }
//
//            when (mangaItems.loadState.append) {
//                is LoadState.Loading -> {
//                    item { LoadingItem() }
//                }
//                is LoadState.Error -> {
//                    item { ErrorItem(message = "Could not load more manga") }
//                }
//                else -> {}
//            }
//        }
//
//        when (mangaItems.loadState.refresh) {
//            is LoadState.Loading -> {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//            }
//            is LoadState.Error -> {
//                ErrorMessage(
//                    message = "Could not load manga. Please check your connection.",
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            }
//            else -> {}
//        }
//    }
//}
//
//@Composable
//fun MangaItem(
//    manga: Manga,
//    onClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(150.dp)
//            .clickable(onClick = onClick),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Row {
//            AsyncImage(
//                model = manga.coverImage,
//                contentDescription = manga.title,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .width(100.dp)
//                    .fillMaxHeight()
//            )
//
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//            ) {
//                Text(
//                    text = manga.title,
//                    style = MaterialTheme.typography.titleMedium,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis
//                )
//
//                Spacer(modifier = Modifier.height(4.dp))
//
//                Text(
//                    text = "By ${manga.isFavorite}",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Text(
//                    text = manga.description,
//                    style = MaterialTheme.typography.bodySmall,
//                    maxLines = 3,
//                    overflow = TextOverflow.Ellipsis
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun LoadingItem() {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        CircularProgressIndicator()
//    }
//}
//
//@Composable
//fun ErrorItem(message: String) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        elevation = CardDefaults.cardElevation(4.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
//    ) {
//        Text(
//            text = message,
//            style = MaterialTheme.typography.bodyMedium,
//            color = MaterialTheme.colorScheme.onErrorContainer,
//            modifier = Modifier.padding(16.dp)
//        )
//    }
//}
//
//@Composable
//fun ErrorMessage(
//    message: String,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        modifier = modifier.padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = message,
//            style = MaterialTheme.typography.bodyLarge,
//            color = MaterialTheme.colorScheme.error
//        )
//    }
//}