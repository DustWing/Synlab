package view

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import enums.Day
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.view.DateVal
import model.view.DayHour
import model.view.DropDownItem
import model.view.User
import model.view.enums.AppStateEnum
import view.components.*
import viewModel.TimeTableViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeTableView() {

    val timeTableViewModel = remember { TimeTableViewModel() }


    val scope = rememberCoroutineScope()

    scope.launch {
        timeTableViewModel.load()
    }


    val focusManager = LocalFocusManager.current


    Scaffold(
        modifier = Modifier.mouseClickable {
            focusManager.clearFocus(true)
        },
        topBar = {
            TopBar(timeTableViewModel)
        },
        bottomBar = {
            BottomBar(timeTableViewModel)
        },
    ) {


        TimeTableScreen(timeTableViewModel)

        if(timeTableViewModel.appState.value==AppStateEnum.LOADING){
            LoadingScreen()
        }


        if (timeTableViewModel.openDialog.value)
            ErrorDialogView(
                openDialog = timeTableViewModel.openDialog,
                title = timeTableViewModel.dialogTitle.value,
                message = timeTableViewModel.dialogMsg.value

            )

        if(timeTableViewModel.showSettingsDialog.value){
            SettingsDialog(
                dropDownItems = timeTableViewModel.preSelectDdItems,
                openDialog = timeTableViewModel.showSettingsDialog,

            ){
                timeTableViewModel.editPreSelectDdItems(it)
            }
        }
    }

}

@Composable
private fun TopBar(
    timeTableViewModel: TimeTableViewModel
) {

    val scope = rememberCoroutineScope()

    Card(
        elevation = 5.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 5.dp, alignment = Alignment.Start)

        ) {
            Button(onClick = {
                timeTableViewModel.addBtn()
            }) {
                Text("ADD")
            }

            IconButton(onClick = {

                timeTableViewModel.showSettingsDialog.value = true

            }) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
            }

            IconButton(onClick = {
                scope.launch {
                    timeTableViewModel.saveSession()
                }
            }) {
                Icon(
                    painter = painterResource("svg/save.svg"),
                    contentDescription = "save",
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun BottomBar(
    timeTableViewModel: TimeTableViewModel
) {
    Card(
        elevation = 5.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 5.dp, alignment = Alignment.End)

        ) {
            Button(onClick = {
                timeTableViewModel.clear()
            }) {
                Text("CLEAR")
            }
            ExcelBtn(timeTableViewModel)
        }
    }
}

@Composable
private fun ExcelBtn(

    timeTableViewModel: TimeTableViewModel

) {

    val scope = rememberCoroutineScope()

    SelectFileButton(
        mode = MODE.SAVE,
        label = "EXCEL",
        validate = {
            timeTableViewModel.validate()
        },
        onFinish = {
            if (it.isNotBlank()) {
                scope.launch(
                    context = Dispatchers.IO
                ) {
                    timeTableViewModel.toExcel(it)
                }
            }
        },
        onError = {
            timeTableViewModel.handleException(it)
        }
    )

}

@Composable
private fun RowScope.TimeTableCell(
    text: String,
    weight: Float,
) {

    Text(
        text = text,
        textAlign = TextAlign.Center,
        fontSize = 16.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier
            .border(1.dp, MaterialTheme.colors.onBackground, shape = RectangleShape)
            .weight(weight)
            .padding(8.dp),

        )
}

@Composable
private fun RowScope.DateInputCell(
    dateVal: DateVal,
    weight: Float,
) {

    TextField(
        value = dateVal.date.value,
        onValueChange = {
            dateVal.date.value = it
        },
        modifier = Modifier
            .border(1.dp, color = MaterialTheme.colors.onBackground)
            .weight(weight)
            .padding(4.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.background,
        ),
        placeholder = { Text("1/12") },
        singleLine = true,
    )
}


@Composable
private fun RowScope.TimeTableInputCell(
    dayHour: DayHour,
    weight: Float,
    items: List<DropDownItem>,
    onFocusChanged: (DayHour) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier.weight(weight).border(1.dp, color = MaterialTheme.colors.onBackground).padding(0.dp)
    ) {
        TextField(
            value = if (dayHour.readOnly.value)  dayHour.dropDownItem.value.label.value else dayHour.hours.value,
            onValueChange = {
                dayHour.hours.value = it
            },
            modifier = Modifier.padding(bottom = 4.dp)
                .onFocusChanged {
                    onFocusChanged(dayHour)
                },
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = Color.Transparent,
                backgroundColor = MaterialTheme.colors.background,
            ),
            placeholder = { Text("00:00-00:00") },
            singleLine = true,
            isError = !dayHour.valid.value,
            readOnly = dayHour.readOnly.value,
            trailingIcon = {


                if (dayHour.readOnly.value) {
                    IconButton(
                        onClick = {
                            dayHour.hours.value = ""
                            dayHour.readOnly.value = false
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Clear, contentDescription = "remove preSelect")
                    }
                } else {
                    IconButton(
                        onClick = {
                            expanded = true
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.List, contentDescription = "preSelect")
                    }
                }

            }
        )

        PreSelectDropDown(
            expanded = expanded,
            items = items,
            onDismissRequest = {
                expanded = false
            },
            onClick = {
                dayHour.dropDownItem.value = it
                dayHour.hours.value = it.value.value
                dayHour.readOnly.value = true
                dayHour.valid.value = true
                onFocusChanged(dayHour)
            }
        )
    }

}


@Composable
private fun PreSelectDropDown(
    expanded: Boolean,
    items: List<DropDownItem>,
    onDismissRequest: (() -> Unit),
    onClick: (DropDownItem) -> Unit,
) {

    val expandedStates = remember { MutableTransitionState(false) }
    expandedStates.targetState = expanded
    if (expandedStates.currentState || expandedStates.targetState) {
        DropdownMenu(
            expanded = true,
            focusable = true,
            onDismissRequest = onDismissRequest,
            offset = DpOffset(50.dp, 0.dp)
        ) {


            items.forEach {
                DropdownMenuItem(
                    onClick = {
                        onClick(it)
                        onDismissRequest()
                    }
                ) {
                    Text(it.label.value)
                }
            }

        }
    }

}

@Composable
private fun RowScope.UserCell(
    user: User,
    weight: Float,
    onRemove: (() -> Unit),
) {

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.height(65.dp).padding(2.dp).weight(weight)
            .clickable {
                expanded = true
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = user.name(),
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
        )
    }

    UserPopUp(
        user = user,
        expanded = expanded,
        onDismissRequest = {
            expanded = false
        },
        onRemove = onRemove
    )

}

@Composable
private fun UserPopUp(
    user: User,
    expanded: Boolean,
    onDismissRequest: (() -> Unit),
    onRemove: (() -> Unit),
) {

    val expandedStates = remember { MutableTransitionState(false) }
    expandedStates.targetState = expanded
    if (expandedStates.currentState || expandedStates.targetState) {
        Popup(
            focusable = true,
            onDismissRequest = onDismissRequest,

            ) {

            Card(
                modifier = Modifier.requiredWidth(300.dp).height(200.dp),
                elevation = 5.dp
            ) {

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {

                    TextField(
                        value = user.firstName.value,
                        onValueChange = {
                            user.firstName.value = it
                        },
                        placeholder = { Text("FirstName") },
                        singleLine = true
                    )

                    Divider(modifier = Modifier.padding(top = 3.dp, bottom = 3.dp))

                    TextField(
                        value = user.lastName.value,
                        onValueChange = {
                            user.lastName.value = it
                        },
                        placeholder = { Text("LastName") },
                        singleLine = true
                    )

                    Button(onClick = {
                        onRemove()
                        onDismissRequest()
                    }) {
                        Text("REMOVE")
                    }
                }

            }

        }
    }

}

@Composable
private fun RowScope.TotalCell(
    total: String,
    weight: Float,
) {

    Box(
        modifier = Modifier.height(65.dp).padding(2.dp).weight(weight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = total,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimeTableScreen(
    timeTableViewModel: TimeTableViewModel
) {

    // Each cell of a column must have the same weight.
    val columnWeight = 1f / 9f

    Column(
        modifier = Modifier.padding(start = 12.dp, end = 12.dp)
    ) {
        // Here is the header
        Card(
            elevation = 0.dp,
            modifier = Modifier.padding(3.dp)
        ) {

            Column {
                Row {
                    Spacer(modifier = Modifier.weight(columnWeight))

                    Day.values().forEach {

                        TimeTableCell(text = it.short(), weight = columnWeight)

                    }

                    Spacer(modifier = Modifier.weight(columnWeight))
                }


                Row {
                    Spacer(modifier = Modifier.weight(columnWeight))
                    timeTableViewModel.dateList.forEach {
                        DateInputCell(dateVal = it, weight = columnWeight)
                    }
                    Spacer(modifier = Modifier.weight(columnWeight))
                }
            }

        }


        Box {
            val state = rememberLazyListState()

            // The LazyColumn will be our table. Notice the use of the weights below
            LazyColumn(
                state = state
            ) {

                // Here are all the lines of your table.
                items(timeTableViewModel.userList) {
                    Card(
                        elevation = 4.dp,
                        modifier = Modifier.padding(3.dp).padding(top = 3.dp),
                    ) {
                        Row(
                            Modifier.fillMaxWidth()
                        ) {
                            UserCell(
                                user = it,
                                weight = columnWeight,
                                onRemove = { timeTableViewModel.removeUserBtn(it) })

                            it.dayHours.forEach { e ->
                                TimeTableInputCell(
                                    dayHour = e,
                                    weight = columnWeight,
                                    items = timeTableViewModel.preSelectDdItems,
                                    onFocusChanged = { dayHour ->
                                        timeTableViewModel.validateCell(dayHour)
                                        timeTableViewModel.calculateUserTotalHours(it)
                                    }
                                )
                            }

                            TotalCell(it.totalHours.value, columnWeight)

                        }
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


    }
}

@Preview
@Composable
fun TimeTableScreen_preview() {
    val timeTableViewModel = remember { TimeTableViewModel() }

    TimeTableScreen(timeTableViewModel)
}