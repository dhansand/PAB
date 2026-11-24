package com.example.coroutinedemo.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

fun Int.toRupiah(): String {
    return "Rp %,d".format(this).replace(',', '.')
}

@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TasksContent(
        state = state,
        onLoad = { viewModel.loadData() },
        onLoadWithError = { viewModel.loadData(shouldFail = true) },
        onCancel = viewModel::cancelLoading,
        onTap = viewModel::onTap,
        onBlock = viewModel::blockMainThread,
        onSort = viewModel::sortTasksByTitle,
        onToggleTask = viewModel::toggleTaskCompletion,
        onSearchChange = viewModel::onSearchQueryChange,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksContent(
    state: TasksUiState,
    onLoad: () -> Unit,
    onLoadWithError: () -> Unit,
    onCancel: () -> Unit,
    onTap: () -> Unit,
    onBlock: () -> Unit,
    onSort: () -> Unit,
    onToggleTask: (Int) -> Unit,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title with Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.ShoppingCart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = "Smart Shopping List",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // Action Buttons Row 1
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onLoad,
                enabled = !state.isLoading
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Load")
            }
            OutlinedButton(
                onClick = onLoadWithError,
                enabled = !state.isLoading
            ) {
                Text("Load (fail)")
            }
            OutlinedButton(
                onClick = onCancel,
                enabled = state.isLoading
            ) {
                Text("Cancel")
            }
        }

        // Action Buttons Row 2
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onTap) {
                Text("Tap: ${state.tapCount}")
            }
            OutlinedButton(onClick = onBlock) {
                Text("Block UI")
            }
            OutlinedButton(
                onClick = onSort,
                enabled = state.tasks.isNotEmpty()
            ) {
                Icon(Icons.Rounded.SortByAlpha, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (state.isSortedAsc) "Sort (Z-A)" else "Sort (A-Z)")
            }
        }

        // Loading Indicator
        if (state.isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Text("Memuat data belanja secara konkuren...", style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Error Message with Retry Icon Button
        state.errorMessage?.let { message ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onLoad) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Retry",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        // Summary Card (Promo, Budget, Total Belanja, Remaining, Duration)
        if (state.announcement.isNotEmpty() || state.budgetLimit != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (state.announcement.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocalOffer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = state.announcement,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }

                    state.budgetLimit?.let { budget ->
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Batas Anggaran:", style = MaterialTheme.typography.bodyMedium)
                            Text(budget.toRupiah(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Belanja Tercentang:", style = MaterialTheme.typography.bodyMedium)
                            Text(state.totalCheckedPrice.toRupiah(), style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Sisa Anggaran:", style = MaterialTheme.typography.titleMedium)
                            val remaining = state.remainingBudget ?: budget
                            Text(
                                text = remaining.toRupiah(),
                                style = MaterialTheme.typography.titleMedium,
                                color = if (remaining < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    state.loadDurationMs?.let { duration ->
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "⚡ Dimuat secara konkuren dalam ${duration} ms",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Search Bar
        if (state.tasks.isNotEmpty()) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchChange,
                label = { Text("Cari barang atau kategori...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // LazyColumn of Shopping Items
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(state.filteredTasks, key = { it.id }) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = item.isCompleted,
                            onCheckedChange = { onToggleTask(item.id) }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                textDecoration = if (item.isCompleted) {
                                    TextDecoration.LineThrough
                                } else {
                                    TextDecoration.None
                                }
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${item.category} • Qty: ${item.quantity} • ${item.price.toRupiah()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
