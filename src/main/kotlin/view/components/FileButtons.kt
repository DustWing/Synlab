package view.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import java.awt.FileDialog


enum class MODE {
    SAVE,
    LOAD,
}

@Composable
fun SelectFileButton(

    modifier: Modifier = Modifier,
    label: String = "select",
    mode: MODE = MODE.LOAD,
    validate: () -> Unit,
    onFinish: (String) -> Unit,
    onError: (Exception) -> Unit
) {


    Button(
        modifier = modifier,
        onClick = {


            try {
                validate()

                val fileDialog: FileDialog = when (mode) {
                    MODE.SAVE -> FileDialog(ComposeWindow(), label, FileDialog.SAVE)
                    MODE.LOAD -> FileDialog(ComposeWindow(), label, FileDialog.LOAD)
                }


                fileDialog.file = "*.xlsx"
                fileDialog.isVisible = true


                onFinish(if (fileDialog.directory == null) "" else fileDialog.directory + fileDialog.file)


            } catch (e: Exception) {

                onError(e)

            }


        })
    {
        Text(
            text = label
        )
    }


}

