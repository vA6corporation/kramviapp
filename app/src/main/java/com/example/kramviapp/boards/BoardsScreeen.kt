package com.example.kramviapp.boards

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.kramviapp.models.NavigateTo
import com.example.kramviapp.navigation.NavigationViewModel
import com.example.kramviapp.ui.theme.KramviRed
import com.example.kramviapp.ui.theme.LightBlue

@Composable
fun BoardsScreen(
    boardsViewModel: BoardsViewModel,
    tablesViewModel: TablesViewModel,
    navigationViewModel: NavigationViewModel,
) {
    val tables by tablesViewModel.tables.collectAsState()
    val boards by boardsViewModel.boards.collectAsState()
    val configuration = LocalConfiguration.current
    var count by remember { mutableIntStateOf(5) }

    count = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            5
        } else -> {
            2
        }
    }

    LaunchedEffect(Unit) {
        navigationViewModel.setTitle("Atencion de mesas")
        boardsViewModel.removeAllBoardItems()
        boardsViewModel.loadActiveBoards()
        if (tables == null) {
            tablesViewModel.loadTables()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(count),
    ) {
        tables?.let { tables ->
            itemsIndexed(tables) {index, table ->
                val board = boards.find { it.tableId == table._id }
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .padding(.3.dp)
                        .background(if (board == null) LightBlue else KramviRed)
                        .clickable {
                            navigationViewModel.onNavigateTo(NavigateTo("posBoard/${index}/false"))
                        },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(text = table.name, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}