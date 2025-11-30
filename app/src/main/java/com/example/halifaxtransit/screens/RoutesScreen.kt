package com.example.halifaxtransit.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.halifaxtransit.MainViewModel
import com.example.halifaxtransit.models.Route

@Composable
fun RoutesScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.loadRoutes(context) }

    val routes = viewModel.routes.collectAsState().value

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        items(routes) { route ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(1.dp, Color.Gray)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {

                    Text(route.routeShortName, modifier = Modifier.padding(top = 4.dp)) // route short name

                    HorizontalDivider( // divider line between number and name
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 1.dp,
                        color = Color.LightGray
                    )

                    Text(route.routeLongName, style = MaterialTheme.typography.bodyMedium)  // route long name
                }

                Checkbox(
                    checked = route.highlights,
                    onCheckedChange = { checked -> //this updates the route id's bool value (1 or 0) aka if its checked
                        viewModel.toggleHighlight(route.routeId, checked) //checked is passing in bool val 1
                    }
                )
            }
        }
    }
}