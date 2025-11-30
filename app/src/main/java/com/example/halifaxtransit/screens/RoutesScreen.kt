package com.example.halifaxtransit.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.halifaxtransit.MainViewModel
import com.example.halifaxtransit.models.Route

@Composable
fun RoutesScreen(viewModel: MainViewModel) {
    val context = LocalContext.current

    // Trigger DB load once when this screen is composed
    LaunchedEffect(Unit) {
        viewModel.loadRoutes(context)   // ✅ pass context positionally
    }

    val routes = viewModel.routes.collectAsState().value

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(routes) { route: Route ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "${route.routeId} ${route.routeLongName}")
                Text(
                    text = route.routeShortName,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}