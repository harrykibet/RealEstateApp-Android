package com.estatia.realestate.apps.feature.profile.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.ui.R

@Composable
fun ProfileScreen(
    profileImage: Painter = painterResource(R.drawable.ic_launcher_round),
    name: String = "Harry Kemboi",
    email: String = "truman948@gmail.com",
    onEditProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Profile Image
        Image(
            painter = profileImage,
            contentDescription = stringResource(R.string.profile_picture),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Email
        Text(
            text = email,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Edit Profile Button
        Button(
            onClick = onEditProfileClick
        ) {
            Text(text = stringResource(R.string.edit_profile))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Divider
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Logout Button
        Button(
            onClick = onLogoutClick
        ) {
            Text(text = stringResource(R.string.logout))
        }
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 400
)
@Composable
fun ProfileScreenLightPreview() {
    EstatiaTheme {
        EstatiaBackground {
            ProfileScreen(
                profileImage = painterResource(id = R.drawable.ic_launcher_round),
                name = "Harry Kemboi",
                email = "truman948@gmail.com"
            )
        }
    }
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 400
)
@Composable
fun ProfileScreenDarkPreview() {
    EstatiaTheme {
        EstatiaBackground {
            ProfileScreen(
                profileImage = painterResource(id = R.drawable.ic_launcher_round),
                name = "Harry Kemboi",
                email = "truman948@gmail.com"
            )
        }
    }
}

