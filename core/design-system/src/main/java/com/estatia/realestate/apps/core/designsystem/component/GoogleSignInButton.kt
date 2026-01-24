package com.estatia.realestate.apps.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.estatia.realestate.apps.core.designsystem.R
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled && !isLoading) { onClick() }, // disable while loading
        color = Color.White.copy(alpha = if (enabled) 1f else 0.5f),
        border = BorderStroke(1.dp, Color.Gray),
        shadowElevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.Black
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.google),
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign in with Google",
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = if (enabled) 1f else 0.5f)
                )
            }
        }
    }
}


@Preview(
    name = "Google Sign-In Button",
    showBackground = true
)
@Composable
fun GoogleSignInButtonPreview() {
     EstatiaTheme {
         EstatiaBackground(
             modifier = Modifier.size(180.dp, 50.dp)
         ) {
             GoogleSignInButton(onClick = {})
         }
    }
}

