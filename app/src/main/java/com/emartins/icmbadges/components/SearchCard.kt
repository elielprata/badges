package com.emartins.icmbadges.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun SearchCard(
    hint: String = "Buscar...",
    onSearch: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 15.dp)
        ) {
            /*TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 2.dp),
                value = query,
                onValueChange = { query = it },
                placeholder = { Text(hint) },
                singleLine = true
            )*/

            MaskedTextField(
                label = "CPF",
                mask = "###.###.###-##",
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                placeholder = hint,
                onDone = { onSearch(query) }
            )

            Spacer(modifier = Modifier.width(6.dp))


            IconButton(
                onClick = {
                    onSearch(query)
                    query = ""
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Buscar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
