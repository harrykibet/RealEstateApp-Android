package com.estatia.realestate.apps.core.designsystem.component

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.designsystem.icons.EstatiaIcons

/**
 * Estatia filled button with generic content slot. Wraps Material 3 [Button].
 * Professionalized with [EstatiaButtonDefaults.ButtonCornerRadius] and brand primary color.
 *
 * @param onClick Will be called when the user clicks the button.
 * @param modifier Modifier to be applied to the button.
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable and will appear disabled to accessibility services.
 * @param contentPadding The spacing values to apply internally between the container and the
 * content.
 * @param content The button content.
 */
@SuppressLint("DesignSystemUsage")
@Composable
fun EstatiaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(EstatiaButtonDefaults.ButtonHeight),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        shape = RoundedCornerShape(EstatiaButtonDefaults.ButtonCornerRadius),
        contentPadding = contentPadding,
        content = content,
    )
}

/**
 * Estatia filled button with text and icon content slots.
 */
@SuppressLint("DesignSystemUsage")
@Composable
fun EstatiaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    EstatiaButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = if (leadingIcon != null) {
            ButtonDefaults.ButtonWithIconContentPadding
        } else {
            ButtonDefaults.ContentPadding
        },
    ) {
        EstatiaButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

/**
 * Estatia outlined button with generic content slot. Wraps Material 3 [OutlinedButton].
 */
@SuppressLint("DesignSystemUsage")
@Composable
fun EstatiaOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(EstatiaButtonDefaults.ButtonHeight),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        shape = RoundedCornerShape(EstatiaButtonDefaults.ButtonCornerRadius),
        border = BorderStroke(
            width = EstatiaButtonDefaults.OutlinedButtonBorderWidth,
            color = if (enabled) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(
                    alpha = EstatiaButtonDefaults.DISABLED_OUTLINED_BUTTON_BORDER_ALPHA,
                )
            },
        ),
        contentPadding = contentPadding,
        content = content,
    )
}

/**
 * Estatia outlined button with text and icon content slots.
 */
@SuppressLint("DesignSystemUsage")
@Composable
fun EstatiaOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    EstatiaOutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = if (leadingIcon != null) {
            ButtonDefaults.ButtonWithIconContentPadding
        } else {
            ButtonDefaults.ContentPadding
        },
    ) {
        EstatiaButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

/**
 * Estatia text button with generic content slot. Wraps Material 3 [TextButton].
 */
@SuppressLint("DesignSystemUsage")
@Composable
fun EstatiaTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        content = content,
    )
}

/**
 * Estatia text button with text and icon content slots.
 */
@SuppressLint("DesignSystemUsage")
@Composable
fun EstatiaTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    EstatiaTextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    ) {
        EstatiaButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

/**
 * Internal Estatia button content layout for arranging the text label and leading icon.
 */
@Composable
private fun EstatiaButtonContent(
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    if (leadingIcon != null) {
        Box(Modifier.sizeIn(maxHeight = ButtonDefaults.IconSize)) {
            leadingIcon()
        }
    }
    Box(
        Modifier
            .padding(
                start = if (leadingIcon != null) {
                    ButtonDefaults.IconSpacing
                } else {
                    0.dp
                },
            ),
    ) {
        text()
    }
}

@ThemePreviews
@Composable
fun EstatiaButtonPreview() {
    EstatiaTheme {
        EstatiaBackground(modifier = Modifier.size(150.dp, 80.dp)) {
            EstatiaButton(onClick = {}, text = { EstatiaText("Test button") })
        }
    }
}

@ThemePreviews
@Composable
fun EstatiaOutlinedButtonPreview() {
    EstatiaTheme {
        EstatiaBackground(modifier = Modifier.size(150.dp, 80.dp)) {
            EstatiaOutlinedButton(onClick = {}, text = { EstatiaText("Test button") })
        }
    }
}

/**
 * Estatia button default values.
 */
object EstatiaButtonDefaults {
    const val DISABLED_OUTLINED_BUTTON_BORDER_ALPHA = 0.12f
    val OutlinedButtonBorderWidth = 1.dp
    val ButtonCornerRadius = 12.dp
    val ButtonHeight = 48.dp
}
