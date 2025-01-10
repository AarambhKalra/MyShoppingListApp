package aarambh.apps.myshoppinglistapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ShoppingItem(
    val id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false
)

@Composable
fun ShoppingListApp() {
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }

    // Cool background with gradient and overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF00B4DB), Color(0xFF0083B0)), // Gradient colors
                    startY = 0f,
                    endY = 1000f
                )
            )
    ) {
        // Semi-transparent overlay effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f)) // Transparent overlay
        )

        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp))
            ) {
                Text(text = "Add Item", fontSize = 18.sp)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                items(sItems) { item ->
                    if (item.isEditing) {
                        ShoppingItemEditor(
                            item = item,
                            onEditComplete = { editedName, editedQuantity ->
                                sItems = sItems.map { it.copy(isEditing = false) }
                                val editedItem = sItems.find { it.id == item.id }
                                editedItem?.let {
                                    it.name = editedName
                                    it.quantity = editedQuantity
                                }
                            }
                        )
                    } else {
                        ShoppingListItem(
                            item = item,
                            onEditClick = { sItems = sItems.map { it.copy(isEditing = it.id == item.id) } },
                            onDeleteClick = { sItems = sItems - item }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                if (itemName.isNotBlank()) {
                                    val newItem = ShoppingItem(
                                        id = sItems.size + 1,
                                        name = itemName,
                                        quantity = itemQuantity.toInt()
                                    )
                                    sItems = sItems + newItem
                                    showDialog = false
                                    itemName = ""
                                    itemQuantity = ""
                                }
                            }
                        ) {
                            Text(text = "Add")
                        }
                        Button(onClick = { showDialog = false }) {
                            Text(text = "Cancel")
                        }
                    }
                },
                title = { Text("Enter Item name and quantity", fontSize = 18.sp) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        OutlinedTextField(
                            value = itemQuantity,
                            onValueChange = { itemQuantity = it },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = Color(0xFFF5F5F5)
            )
        }
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(15.dp))
            .background(Color.White)
            .border(
                border = BorderStroke(1.dp, Color(0xFF018786)),
                shape = RoundedCornerShape(15.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = item.name, fontSize = 20.sp, modifier = Modifier.padding(4.dp))
            Text(text = "Qty: ${item.quantity}", fontSize = 16.sp, color = Color.Gray)
        }
        Row {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun ShoppingItemEditor(item: ShoppingItem, onEditComplete: (String, Int) -> Unit) {
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column {
            BasicTextField(
                value = editedName,
                onValueChange = { editedName = it },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )

            BasicTextField(
                value = editedQuantity,
                onValueChange = { editedQuantity = it },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
        }
        Button(
            onClick = {
                onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1)
            }
        ) {
            Text("Save")
        }
    }
}
