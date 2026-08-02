package br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.Color
import br.edu.utfpr.pb.tcc_daviaugustolira.shared.ui.FontSize

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String = "",
    keyboardType: KeyboardType? = null,
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    trailingIcon: @Composable () -> Unit = {},
    visualTransformation: VisualTransformation? = null,
) {
    TextField(
        modifier =
            modifier
                .width(130.dp)
                .border(
                    0.8.dp,
                    color = Color.BORDER_INPUT,
                    shape = RoundedCornerShape(size = 24.dp),
                ).clip(RoundedCornerShape(size = 24.dp)),
        label = {
            Text(
                text = label,
                maxLines = 1,
                fontSize = FontSize.MEDIUM,
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                maxLines = 1,
                fontSize = FontSize.SMALL,
            )
        },
        value = value,
        onValueChange = { valor ->
            onValueChange(valor)
        },
        enabled = true,
        singleLine = true,
        shape = RoundedCornerShape(size = 8.dp),
        keyboardOptions =
            KeyboardOptions(
                keyboardType = keyboardType ?: KeyboardType.Ascii,
            ),
        visualTransformation = visualTransformation ?: VisualTransformation.None,
        colors =
            TextFieldDefaults.colors(
                unfocusedContainerColor =
                    androidx.compose.ui.graphics
                        .Color(0XFFFAFAFA),
                focusedContainerColor = Color.BACKGROUND_INPUT,
                focusedLabelColor = Color.TEXT_INPUT,
                focusedPlaceholderColor = Color.TEXT_INPUT,
                focusedSuffixColor = androidx.compose.ui.graphics.Color.Transparent,
                focusedPrefixColor = androidx.compose.ui.graphics.Color.Transparent,
                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedPlaceholderColor = androidx.compose.ui.graphics.Color.Black,
                unfocusedLabelColor = androidx.compose.ui.graphics.Color.Black,
                unfocusedPrefixColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedTrailingIconColor = androidx.compose.ui.graphics.Color.Transparent,
                selectionColors =
                    TextSelectionColors(
                        handleColor = Color.ORANGE_20,
                        backgroundColor = Color.ORANGE_10.copy(alpha = 0.3f),
                    ),
                cursorColor = androidx.compose.ui.graphics.Color.Black,
            ),
        readOnly = readOnly,
        trailingIcon = trailingIcon,
    )
}
