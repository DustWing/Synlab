package view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import model.view.DropDownItem

@Composable
fun ErrorDialogView(
    openDialog: MutableState<Boolean>,
    title: String = "Title",
    message: String = "message",

    ) {
    Dialog(
        title = title,
        onCloseRequest = {
            openDialog.value = false
        }
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            Text(message)

            Button(onClick = {
                openDialog.value = false
            }) {
                Text("OK")
            }
        }

    }
}

