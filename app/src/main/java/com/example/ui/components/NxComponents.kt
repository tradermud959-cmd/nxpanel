package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalNxColors
import com.example.ui.theme.MonocraftFontFamily
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.ZeroPrimeFontFamily

/**
 * NxCard:
 * Box (padding kanan-bawah untuk ruang shadow) {
 *   Box matchParentSize offset kanan-bawah bg #E8E8E8 (SHADOW);
 *   Column bg cardFill + border + padding 16dp
 * }
 * Card CERAH (isBright = true): fill accent, text hitam, border hitam 3dp, shadow #E8E8E8 6dp.
 * Card GELAP (isBright = false): fill cardDark, text terang, border accent 2.5dp, shadow #E8E8E8 5dp.
 */
@Composable
fun NxCard(
    modifier: Modifier = Modifier,
    isBright: Boolean = false,
    cornerRadius: Dp = 12.dp,
    contentPadding: Dp = 16.dp,
    customFillColor: Color? = null,
    customBorderColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val nxColors = LocalNxColors.current
    val shadowOffset = if (isBright) 6.dp else 5.dp
    val borderWidth = if (isBright) 3.dp else 2.5.dp
    val fillColor = customFillColor ?: if (isBright) nxColors.accent else nxColors.cardDark
    val borderColor = customBorderColor ?: if (isBright) nxColors.borderBlack else nxColors.accent
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier.padding(end = shadowOffset, bottom = shadowOffset)
    ) {
        // Hard Offset Shadow (TANPA BLUR, warna putih tulang #E8E8E8)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(color = nxColors.shadow, shape = shape)
        )

        val columnModifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(color = fillColor, shape = shape)
            .border(width = borderWidth, color = borderColor, shape = shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .padding(contentPadding)

        Column(
            modifier = columnModifier,
            content = content
        )
    }
}

/**
 * NxButton:
 * Border hitam tebal (3dp) + fill accent + text hitam + hard shadow putih tulang (#E8E8E8) 4dp.
 */
@Composable
fun NxButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isDarkVariant: Boolean = false,
    fillColorOverride: Color? = null,
    textColorOverride: Color? = null,
    borderColorOverride: Color? = null,
    cornerRadius: Dp = 10.dp,
    shadowOffset: Dp = 4.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    enabled: Boolean = true
) {
    val nxColors = LocalNxColors.current
    val shape = RoundedCornerShape(cornerRadius)

    val bgFill = fillColorOverride ?: if (isDarkVariant) nxColors.cardDark else nxColors.accent
    val txtColor = textColorOverride ?: if (isDarkVariant) nxColors.cardDarkText else nxColors.cardBrightText
    val borderColor = borderColorOverride ?: if (isDarkVariant) nxColors.accent else nxColors.borderBlack
    val borderWidth = if (isDarkVariant) 2.5.dp else 3.dp

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .padding(end = shadowOffset, bottom = shadowOffset)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(color = nxColors.shadow, shape = shape)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(
                    color = if (enabled) bgFill else bgFill.copy(alpha = 0.55f),
                    shape = shape
                )
                .border(width = borderWidth, color = borderColor, shape = shape)
                .clickable(enabled = enabled) { onClick() }
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = if (isDarkVariant) nxColors.accent else txtColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                color = txtColor,
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * NxIconButton:
 * Tombol ikon kotak bergaya Neo-Brutalist 3D Comic dengan hard shadow putih tulang.
 */
@Composable
fun NxIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isAccentFill: Boolean = false,
    size: Dp = 44.dp
) {
    val nxColors = LocalNxColors.current
    val shape = RoundedCornerShape(10.dp)
    val shadowOffset = 4.dp
    val bgFill = if (isAccentFill) nxColors.accent else nxColors.cardDark
    val iconTint = if (isAccentFill) nxColors.cardBrightText else nxColors.cardDarkText
    val borderColor = if (isAccentFill) nxColors.borderBlack else nxColors.borderBlack

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .padding(end = shadowOffset, bottom = shadowOffset)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .offset(x = shadowOffset, y = shadowOffset)
                .background(color = nxColors.shadow, shape = shape)
        )
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(color = bgFill, shape = shape)
                .border(width = 2.5.dp, color = borderColor, shape = shape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/**
 * NxComicAppIconFrame:
 * Ikon R.drawable.icon_apps dibungkus frame 3D Comic (border hitam 3dp, hard shadow #E8E8E8).
 */
@Composable
fun NxComicAppIconFrame(
    size: Dp = 48.dp,
    shadowOffset: Dp = 4.dp,
    cornerRadius: Dp = 12.dp,
    modifier: Modifier = Modifier
) {
    val nxColors = LocalNxColors.current
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier.padding(end = shadowOffset, bottom = shadowOffset)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .offset(x = shadowOffset, y = shadowOffset)
                .background(color = nxColors.shadow, shape = shape)
        )
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(color = nxColors.cardDark, shape = shape)
                .border(width = 3.dp, color = nxColors.borderBlack, shape = shape)
                .padding(3.dp)
                .border(width = 1.5.dp, color = nxColors.accent, shape = RoundedCornerShape(cornerRadius - 2.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_apps),
                contentDescription = "NXPANEL App Icon",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius - 3.dp)),
                contentScale = ContentScale.Fit
            )
        }
    }
}

/**
 * NxBadge:
 * Pill/tag dengan font Monocraft Black, border hitam tebal, dan hard shadow.
 */
@Composable
fun NxBadge(
    text: String,
    modifier: Modifier = Modifier,
    fillColor: Color? = null,
    textColor: Color? = null,
    shadowOffset: Dp = 3.dp
) {
    val nxColors = LocalNxColors.current
    val shape = RoundedCornerShape(8.dp)
    val bg = fillColor ?: nxColors.accent
    val fg = textColor ?: nxColors.cardBrightText

    Box(
        modifier = modifier.padding(end = shadowOffset, bottom = shadowOffset)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(color = nxColors.shadow, shape = shape)
        )
        Box(
            modifier = Modifier
                .clip(shape)
                .background(color = bg, shape = shape)
                .border(width = 2.5.dp, color = nxColors.borderBlack, shape = shape)
                .padding(horizontal = 10.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = fg,
                fontFamily = MonocraftFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp
            )
        }
    }
}

enum class NxNotificationType {
    ERROR,
    SUCCESS,
    INFO
}

data class NxNotificationState(
    val visible: Boolean = false,
    val message: String = "",
    val type: NxNotificationType = NxNotificationType.INFO
)

/**
 * NxNotification:
 * Muncul dari atas, pill/capsule, animasi slide down + scale-in -> diam 2-3 detik -> slide up.
 */
@Composable
fun NxNotificationBanner(
    state: NxNotificationState,
    modifier: Modifier = Modifier
) {
    val nxColors = LocalNxColors.current
    val capsuleShape = CircleShape
    val shadowOffset = 4.dp

    AnimatedVisibility(
        visible = state.visible,
        enter = slideInVertically(
            initialOffsetY = { -it - 40 },
            animationSpec = tween(durationMillis = 280)
        ) + scaleIn(
            initialScale = 0.85f,
            animationSpec = tween(durationMillis = 280)
        ) + fadeIn(animationSpec = tween(durationMillis = 200)),
        exit = slideOutVertically(
            targetOffsetY = { -it - 40 },
            animationSpec = tween(durationMillis = 250)
        ) + scaleOut(
            targetScale = 0.85f,
            animationSpec = tween(durationMillis = 250)
        ) + fadeOut(animationSpec = tween(durationMillis = 200)),
        modifier = modifier.testTag("nx_notification_banner")
    ) {
        val fillColor = when (state.type) {
            NxNotificationType.ERROR -> nxColors.warning
            NxNotificationType.SUCCESS -> nxColors.accent
            NxNotificationType.INFO -> nxColors.cardDark
        }
        val textColor = when (state.type) {
            NxNotificationType.ERROR -> nxColors.borderBlack
            NxNotificationType.SUCCESS -> nxColors.cardBrightText
            NxNotificationType.INFO -> nxColors.cardDarkText
        }
        val iconVector = when (state.type) {
            NxNotificationType.ERROR -> Icons.Default.Error
            NxNotificationType.SUCCESS -> Icons.Default.CheckCircle
            NxNotificationType.INFO -> Icons.Default.Info
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(end = shadowOffset, bottom = shadowOffset)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(color = nxColors.shadow, shape = capsuleShape)
            )
            Row(
                modifier = Modifier
                    .clip(capsuleShape)
                    .background(color = fillColor, shape = capsuleShape)
                    .border(width = 3.dp, color = nxColors.borderBlack, shape = capsuleShape)
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = state.type.name,
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = state.message,
                    color = textColor,
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * Helper Composable for ZeroPrime Halftone Italic Title with Comic Outline/Shadow effect
 */
@Composable
fun NxHalftoneTitle(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 20,
    textColor: Color? = null
) {
    val nxColors = LocalNxColors.current
    val fg = textColor ?: nxColors.cardDarkText

    Box(modifier = modifier) {
        Text(
            text = text,
            color = nxColors.borderBlack,
            fontFamily = ZeroPrimeFontFamily,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            fontSize = fontSize.sp,
            letterSpacing = 1.2.sp,
            modifier = Modifier.offset(x = 2.dp, y = 2.dp)
        )
        Text(
            text = text,
            color = fg,
            fontFamily = ZeroPrimeFontFamily,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            fontSize = fontSize.sp,
            letterSpacing = 1.2.sp
        )
    }
}

/**
 * Helper Composable for Monocraft Black technical value display
 */
@Composable
fun NxMonoValue(
    text: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    fontSize: Int = 14
) {
    val nxColors = LocalNxColors.current
    Text(
        text = text.ifBlank { "N/A" },
        color = color ?: nxColors.cardDarkText,
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize.sp,
        modifier = modifier
    )
}
