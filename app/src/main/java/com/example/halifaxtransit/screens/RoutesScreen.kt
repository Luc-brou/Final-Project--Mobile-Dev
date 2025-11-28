package com.example.halifaxtransit.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.halifaxtransit.MainViewModel
import com.example.halifaxtransit.models.Route

@Composable
fun RoutesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val routes = viewModel.routes.collectAsState().value
    val selectedRoutes = viewModel.selectedRoutes.collectAsState().value

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Available Routes",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Select routes to highlight on the map",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(routes) { route: Route ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(route.routeShortName, fontWeight = FontWeight.Bold)
                            Text("Route ID: ${route.routeId}")
                        }
                        Checkbox(
                            checked = selectedRoutes.contains(route.routeId),
                            onCheckedChange = { viewModel.toggleRouteSelection(route.routeId) }
                        )
                    }
                }
            }
        }
    }
}