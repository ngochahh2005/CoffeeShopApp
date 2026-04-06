package com.example.coffeeshopapp.presentation.screen.admin.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.coffeeshopapp.data.model.dto.CategoryDto
import com.example.coffeeshopapp.presentation.viewmodel.AdminCategoryScreenType
import com.example.coffeeshopapp.presentation.viewmodel.AdminCategoryViewModel
import com.example.coffeeshopapp.presentation.viewmodel.CategoryUiState

@Composable
fun AdminCategoryScreen(
    viewModel: AdminCategoryViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showDeleteConfirmDialog && uiState.selectedCategory != null) {
        DeleteConfirmationDialog(
            category = uiState.selectedCategory!!,
            onConfirm = { viewModel.confirmDelete() },
            onDismiss = { viewModel.dismissDeleteDialog() }
        )
    }

    when (uiState.currentScreen) {
        AdminCategoryScreenType.LIST -> {
            CategoryListScreen(
                uiState = uiState,
                onAddClick = { viewModel.navigateTo(AdminCategoryScreenType.CREATE) },
                onDetailClick = { viewModel.navigateTo(AdminCategoryScreenType.DETAIL, it) },
                onEditClick = { viewModel.navigateTo(AdminCategoryScreenType.UPDATE, it) },
                onDeleteClick = { viewModel.showDeleteDialog(it) },
                onBackClick = onBackClick
            )
        }
        AdminCategoryScreenType.DETAIL -> {
            CategoryDetailScreen(
                category = uiState.selectedCategory,
                onBack = { viewModel.navigateTo(AdminCategoryScreenType.LIST) },
                onEdit = { viewModel.navigateTo(AdminCategoryScreenType.UPDATE, uiState.selectedCategory) }
            )
        }
        AdminCategoryScreenType.CREATE -> {
            CategoryFormScreen(
                isUpdating = false,
                initialCategory = null,
                onSubmit = { name, desc -> viewModel.createCategory(name, desc) },
                onBack = { viewModel.navigateTo(AdminCategoryScreenType.LIST) }
            )
        }
        AdminCategoryScreenType.UPDATE -> {
            CategoryFormScreen(
                isUpdating = true,
                initialCategory = uiState.selectedCategory,
                onSubmit = { name, desc -> 
                    val id = uiState.selectedCategory?.id ?: return@CategoryFormScreen
                    viewModel.updateCategory(id, name, desc) 
                },
                onBack = { viewModel.navigateTo(AdminCategoryScreenType.LIST) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    uiState: CategoryUiState,
    onAddClick: () -> Unit,
    onDetailClick: (CategoryDto) -> Unit,
    onEditClick: (CategoryDto) -> Unit,
    onDeleteClick: (CategoryDto) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Category Manager") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(uiState.categories) { category ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        onClick = { onDetailClick(category) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = category.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                category.description?.let {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = it, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                }
                            }
                            Row {
                                IconButton(onClick = { onEditClick(category) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { onDeleteClick(category) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    category: CategoryDto?,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Category Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { padding ->
        if (category == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Category not found")
            }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Name", fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(category.name, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Description", fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(category.description ?: "N/A", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreen(
    isUpdating: Boolean,
    initialCategory: CategoryDto?,
    onSubmit: (String, String?) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(initialCategory?.name ?: "") }
    var description by remember { mutableStateOf(initialCategory?.description ?: "") }
    val screenTitle = if (isUpdating) "Update Category" else "Add Category"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Category Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { onSubmit(title, description.ifEmpty { null }) },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text(if (isUpdating) "Update" else "Save")
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    category: CategoryDto,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Delete Category")
        },
        text = {
            Text("Are you sure you want to delete ${category.name}? This action cannot be undone.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}
