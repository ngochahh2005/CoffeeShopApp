package com.example.coffeeshopapp.presentation.screen.user

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.data.TokenProvider
import com.example.coffeeshopapp.data.local.AuthDataStore
import com.example.coffeeshopapp.data.model.dto.UserResponseDto
import com.example.coffeeshopapp.data.remote.LogoutRequestDto
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.utils.getErrorMessage
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
fun ProfileScreen(
    onOpenAdmin: () -> Unit = {},
    onOpenChangePassword: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val rolesFromStore by AuthDataStore.rolesFlow(context).collectAsState(initial = emptyList())
    val providerFromStore by AuthDataStore.providerFlow(context).collectAsState(initial = "LOCAL")

    var userInfo by remember { mutableStateOf<UserResponseDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    suspend fun reloadProfile() {
        val resp = NetworkClient.api.getMyInfoFull()
        userInfo = resp.result
        resp.result?.roles
            ?.mapNotNull { it.name ?: it.code }
            ?.takeIf { it.isNotEmpty() }
            ?.let { AuthDataStore.setRoles(context, it) }
        AuthDataStore.setProvider(context, resp.result?.provider ?: providerFromStore)
    }

    LaunchedEffect(Unit) {
        try {
            reloadProfile()
        } catch (e: Exception) {
            Toast.makeText(context, e.getErrorMessage(), Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(userInfo?.id, isEditing) {
        if (!isEditing) {
            fullName = userInfo?.fullName.orEmpty()
            phoneNumber = userInfo?.phoneNumber.orEmpty()
            dob = userInfo?.dob.orEmpty()
        }
    }

    val rolesLabel = userInfo?.roles
        ?.mapNotNull { it.name ?: it.code }
        ?.takeIf { it.isNotEmpty() }
        ?.joinToString(", ")
        ?: rolesFromStore.ifEmpty { listOf("USER") }.joinToString(", ")
    val isAdmin = rolesLabel.contains("ADMIN", ignoreCase = true)
    val actualProvider = userInfo?.provider ?: providerFromStore
    val isGoogleAccount = actualProvider.equals("GOOGLE", ignoreCase = true)

    val gradientColors = listOf(
        Color(0xFF7B6BA8),
        Color(0xFF9B8FC2),
        Color(0xFFB8A9D8)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F3FA))
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeader(
            userInfo = userInfo,
            gradientColors = gradientColors
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(32.dp)
            )
        } else {
            ProfileSectionCard(
                title = "Thông tin cá nhân",
                trailing = {
                    if (isEditing) {
                        IconButton(
                            onClick = {
                                isEditing = false
                                fullName = userInfo?.fullName.orEmpty()
                                phoneNumber = userInfo?.phoneNumber.orEmpty()
                                dob = userInfo?.dob.orEmpty()
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Hủy", tint = Color(0xFF8278A0))
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa", tint = Color(0xFF8278A0))
                        }
                    }
                }
            ) {
                if (isEditing) {
                    EditableProfileContent(
                        fullName = fullName,
                        onFullNameChange = { fullName = it },
                        email = userInfo?.email ?: "Chưa cập nhật",
                        phoneNumber = phoneNumber,
                        onPhoneChange = { phoneNumber = it },
                        dob = dob,
                        onDobChange = { dob = it },
                        rolesLabel = rolesLabel,
                        isSaving = isSaving,
                        onSave = {
                            val trimmedName = fullName.trim()
                            if (trimmedName.isBlank()) {
                                Toast.makeText(context, "Vui lòng nhập họ và tên!", Toast.LENGTH_SHORT).show()
                                return@EditableProfileContent
                            }
                            if (dob.isNotBlank() && !Regex("\\d{4}-\\d{2}-\\d{2}").matches(dob)) {
                                Toast.makeText(context, "Ngày sinh cần có dạng yyyy-MM-dd!", Toast.LENGTH_SHORT).show()
                                return@EditableProfileContent
                            }

                            val firstName = trimmedName.substringBeforeLast(" ", trimmedName)
                            val lastName = trimmedName.substringAfterLast(" ", "")
                            isSaving = true
                            coroutineScope.launch {
                                try {
                                    val resp = NetworkClient.api.updateMyInfo(
                                        firstName = firstName.toPlainPart(),
                                        lastName = lastName.toPlainPart(),
                                        phoneNumber = phoneNumber.blankToNullPart(),
                                        dob = dob.blankToNullPart()
                                    )
                                    userInfo = resp.result
                                    isEditing = false
                                    Toast.makeText(context, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, e.getErrorMessage(), Toast.LENGTH_SHORT).show()
                                } finally {
                                    isSaving = false
                                }
                            }
                        }
                    )
                } else {
                    ProfileInfoRow(Icons.Default.Person, "Họ và tên", userInfo?.fullName ?: "Chưa cập nhật")
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    ProfileInfoRow(Icons.Default.Email, "Email", userInfo?.email ?: "Chưa cập nhật")
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    ProfileInfoRow(Icons.Default.Phone, "Số điện thoại", userInfo?.phoneNumber ?: "Chưa cập nhật")
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    ProfileInfoRow(Icons.Default.CalendarToday, "Ngày sinh", userInfo?.dob ?: "Chưa cập nhật")
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    ProfileInfoRow(Icons.Default.Badge, "Vai trò", rolesLabel)
                    if (isGoogleAccount) {
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                        ProfileInfoRow(Icons.Default.Cloud, "Đăng nhập qua", "Google")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ProfileSectionCard(title = "Cài đặt") {
                if (!isGoogleAccount) {
                    ProfileActionRow(
                        icon = Icons.Default.Lock,
                        label = "Đổi mật khẩu",
                        onClick = onOpenChangePassword
                    )
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                }

                if (isAdmin) {
                    ProfileActionRow(
                        icon = Icons.Default.AdminPanelSettings,
                        label = "Trang quản trị",
                        onClick = onOpenAdmin,
                        tint = Color(0xFF0058BC)
                    )
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                }

                ProfileActionRow(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    label = "Đăng xuất",
                    onClick = {
                        coroutineScope.launch {
                            try {
                                TokenProvider.token?.let {
                                    NetworkClient.api.logout(LogoutRequestDto(it))
                                }
                            } catch (_: Exception) {
                                // Logout local vẫn được xử lý bên dưới.
                            }
                            AuthDataStore.clearAll(context)
                            TokenProvider.token = null
                            TokenProvider.refreshToken = null
                            Toast.makeText(context, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
                            onLogout()
                        }
                    },
                    tint = Color(0xFFDC2626)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileHeader(
    userInfo: UserResponseDto?,
    gradientColors: List<Color>
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                brush = Brush.verticalGradient(gradientColors),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(24.dp))

            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (!userInfo?.avt.isNullOrBlank()) {
                    AsyncImage(
                        model = userInfo?.avt,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(84.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = userInfo?.fullName ?: userInfo?.username ?: "Đang tải...",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Text(
                text = userInfo?.email ?: "",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun EditableProfileContent(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    email: String,
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    dob: String,
    onDobChange: (String) -> Unit,
    rolesLabel: String,
    isSaving: Boolean,
    onSave: () -> Unit
) {
    OutlinedTextField(
        value = fullName,
        onValueChange = onFullNameChange,
        label = { Text("Họ và tên") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(10.dp))
    ReadOnlyField(label = "Email", value = email)
    Spacer(modifier = Modifier.height(10.dp))
    OutlinedTextField(
        value = phoneNumber,
        onValueChange = onPhoneChange,
        label = { Text("Số điện thoại") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(10.dp))
    OutlinedTextField(
        value = dob,
        onValueChange = onDobChange,
        label = { Text("Ngày sinh (yyyy-MM-dd)") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(10.dp))
    ReadOnlyField(label = "Vai trò", value = rolesLabel)
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = onSave,
        enabled = !isSaving,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B6BA8)),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isSaving) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Lưu thay đổi", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ReadOnlyField(label: String, value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        singleLine = true,
        enabled = false,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun ProfileSectionCard(
    title: String,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF3D3450),
                    modifier = Modifier.weight(1f)
                )
                trailing?.invoke()
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = Color(0xFF8278A0)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 12.sp, color = Color(0xFF999999))
            Text(text = value, fontSize = 15.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ProfileActionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: Color = Color(0xFF8278A0)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp), tint = tint)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = tint,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFCCCCCC),
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun String.toPlainPart(): RequestBody =
    toRequestBody("text/plain".toMediaTypeOrNull())

private fun String.blankToNullPart(): RequestBody? =
    takeIf { it.isNotBlank() }?.toPlainPart()
