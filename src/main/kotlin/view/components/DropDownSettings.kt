package view.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import model.view.DropDownItem


@Composable
private fun Item(
    item: DropDownItem,
    onRemove: () -> Unit
) {
    Card(
        elevation = 4.dp,
        modifier = Modifier.padding(3.dp).padding(top = 3.dp),
    ) {
        Row(
            Modifier.fillMaxWidth()
        ) {


            TextField(
                value = item.label.value,
                onValueChange = {
                    item.label.value = it
                },
                modifier = Modifier
                    .border(1.dp, color = MaterialTheme.colors.onBackground)
                    .padding(4.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.background,
                ),
                placeholder = { Text("Label") },
                singleLine = true,
            )

            TextField(
                value = item.value.value,
                onValueChange = {
                    item.value.value = it
                },
                modifier = Modifier
                    .border(1.dp, color = MaterialTheme.colors.onBackground)
                    .padding(4.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.background,
                ),
                placeholder = { Text("Value") },
                singleLine = true,
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = item.addInTotal.value,
                    onCheckedChange = { item.addInTotal.value = it }
                )
                Text("Total", textAlign = TextAlign.Center)
            }


            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Checkbox(
                    checked = item.addInDay.value,
                    onCheckedChange = { item.addInDay.value = it }
                )
                Text("Day", textAlign = TextAlign.Center)
            }


            IconButton(onClick = {
                onRemove()
            }) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete")
            }


        }
    }
}

@Composable
fun SettingsDialog(
    dropDownItems: List<DropDownItem>,
    openDialog: MutableState<Boolean>,
    title: String = "Settings",
    onSubmit: (List<DropDownItem>) -> Unit
) {

    val data = mutableStateListOf<DropDownItem>()
    dropDownItems.forEach {
        data.add(it.copy())
    }

    Dialog(
        title = title,
        onCloseRequest = {
            openDialog.value = false
        },
        state = DialogState(size = DpSize(800.dp, 600.dp)),
        resizable = false
    ) {

        Column {

            Row (Modifier.weight(0.1f)){
                Button(
                    onClick = {
                        data.add(
                            DropDownItem(
                                label = mutableStateOf(""),
                                value = mutableStateOf(""),
                                addInTotal = mutableStateOf(false),
                                addInDay = mutableStateOf(false)
                            )
                        )
                    }
                ) {
                    Text("ADD")
                }
            }


            Box (Modifier.weight(0.8f)){
                val state = rememberLazyListState()

                // The LazyColumn will be our table. Notice the use of the weights below
                LazyColumn(
                    state = state
                ) {

                    // Here are all the lines of your table.
                    items(data) {

                        Item(it) {
                            data.remove(it)
                        }

                    }
                }


                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = state
                    )
                )
            }

            Row (Modifier.weight(0.1f)){
                Button(
                    onClick = {
                        onSubmit(data)
                    }
                ) {
                    Text("OK")
                }
            }

        }


    }
}