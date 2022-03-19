package view.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlertDialogView(
    openDialog: MutableState<Boolean>
) {
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
            openDialog.value = false
        },
        title = {
            Text(text = "Dialog Title")
        },
        text = {
            Text("Here is a text ")
        },
        confirmButton = {
            Button(

                onClick = {
                    openDialog.value = false
                }) {
                Text("This is the Confirm Button")
            }
        },
        dismissButton = {

            Button(

                onClick = {
                    println("AlertDialogView dismiss")
                    openDialog.value = false
                }) {
                Text("This is the dismiss Button")
            }
        }
    )
}