package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.dto.UserPhotoDTO
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.AlbumViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * 编辑相册Activity
 * 支持上传、删除照片，设置头像等功能
 */
class EditAlbumActivity : ComponentActivity() {
    
    private lateinit var albumViewModel: AlbumViewModel
    private var tempImageFile: File? = null
    
    // 权限请求
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: false
        val hasStoragePermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 使用新的媒体权限
            permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false
        } else {
            // Android 12 及以下使用传统存储权限
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
        }
        
        android.util.Log.d("EditAlbumActivity", "权限请求结果 - 相机: $hasCameraPermission, 存储: $hasStoragePermission")
        
        if (hasStoragePermission && hasCameraPermission) {
            openImagePicker()
        } else {
            // 权限被拒绝，显示更详细的说明
            showPermissionDeniedDialog()
        }
    }
    
    private fun showPermissionDeniedDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("权限被拒绝")
            .setMessage("相机权限被拒绝，无法上传照片。\n\n请到 设置 > 应用权限 > 相机 中手动开启权限")
            .setPositiveButton("去设置") { _, _ ->
                // 打开应用设置页面
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = android.net.Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    // 选择图片
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            imageUri?.let { uri ->
                uploadImage(uri)
            }
        }
    }
    
    // 拍照
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            tempImageFile?.let { file ->
                uploadImage(Uri.fromFile(file))
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        albumViewModel = AlbumViewModel(this.applicationContext)
        
        setContent {
            MyApplicationTheme {
                EditAlbumScreen(
                    onBackClick = { finish() },
                    onSaveClick = { 
                        Toast.makeText(this, "相册已保存", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onAddPhotoClick = { 
                        checkPermissionAndOpenPicker()
                    },
                    onDeletePhotoClick = { photoId ->
                        albumViewModel.deletePhoto(photoId)
                    },
                    onSetAvatarClick = { photoId ->
                        albumViewModel.setAsAvatar(photoId)
                    },
                    viewModel = albumViewModel
                )
            }
        }
        
        // 加载用户照片
        albumViewModel.loadUserPhotos()
    }
    
    private fun checkPermissionAndOpenPicker() {
        android.util.Log.d("EditAlbumActivity", "开始检查权限")
        android.util.Log.d("EditAlbumActivity", "Android版本: ${android.os.Build.VERSION.SDK_INT}")
        
        val hasCameraPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        
        val hasStoragePermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 使用新的媒体权限
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android 12 及以下使用传统存储权限
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        
        android.util.Log.d("EditAlbumActivity", "存储权限: $hasStoragePermission")
        android.util.Log.d("EditAlbumActivity", "相机权限: $hasCameraPermission")
        
        when {
            hasStoragePermission && hasCameraPermission -> {
                android.util.Log.d("EditAlbumActivity", "权限检查通过，显示选择方式对话框")
                // 两个权限都有，显示选择方式对话框
                showImageSourceDialog()
            }
            else -> {
                android.util.Log.d("EditAlbumActivity", "权限不足，显示权限说明对话框")
                // 缺少权限，显示权限说明对话框
                showPermissionDialog()
            }
        }
    }
    
    private fun showImageSourceDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("选择图片来源")
            .setItems(arrayOf("相册", "相机")) { _, which ->
                when (which) {
                    0 -> {
                        android.util.Log.d("EditAlbumActivity", "用户选择相册")
                        openImagePicker() // 相册
                    }
                    1 -> {
                        android.util.Log.d("EditAlbumActivity", "用户选择相机")
                        openCamera()      // 相机
                    }
                }
            }
            .setNegativeButton("取消") { _, _ ->
                android.util.Log.d("EditAlbumActivity", "用户取消选择")
            }
            .show()
    }
    
    private fun openCamera() {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            tempImageFile = File.createTempFile("camera_image", ".jpg", cacheDir)
            val photoURI = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                tempImageFile!!
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            cameraLauncher.launch(intent)
        } catch (e: Exception) {
            android.util.Log.e("EditAlbumActivity", "打开相机失败: ${e.message}", e)
            Toast.makeText(this, "无法打开相机: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showPermissionDialog() {
        val permissions = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 使用新的媒体权限
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.CAMERA
            )
        } else {
            // Android 12 及以下使用传统存储权限
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }
        
        val permissionNames = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            "相机和媒体权限"
        } else {
            "相机和存储权限"
        }
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("需要权限")
            .setMessage("需要相机和存储权限才能上传照片，请在设置中开启相关权限")
            .setPositiveButton("去设置") { _, _ ->
                android.util.Log.d("EditAlbumActivity", "请求权限: ${permissions.joinToString()}")
                requestPermissionLauncher.launch(permissions)
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun openImagePicker() {
        try {
            android.util.Log.d("EditAlbumActivity", "开始打开图片选择器")
            android.util.Log.d("EditAlbumActivity", "当前Context: ${this::class.java.simpleName}")
            android.util.Log.d("EditAlbumActivity", "isFinishing: $isFinishing")
            android.util.Log.d("EditAlbumActivity", "isDestroyed: $isDestroyed")
            
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            
            android.util.Log.d("EditAlbumActivity", "Intent创建成功，准备启动")
            imagePickerLauncher.launch(Intent.createChooser(intent, "选择图片"))
            android.util.Log.d("EditAlbumActivity", "图片选择器启动成功")
        } catch (e: Exception) {
            android.util.Log.e("EditAlbumActivity", "打开图片选择器失败: ${e.message}", e)
            Toast.makeText(this, "无法打开图片选择器: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun uploadImage(uri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir)
            val outputStream = FileOutputStream(tempFile)
            
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            albumViewModel.uploadPhoto(tempFile)
        } catch (e: Exception) {
            Toast.makeText(this, "图片处理失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun EditAlbumScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onAddPhotoClick: () -> Unit,
    onDeletePhotoClick: (Long) -> Unit,
    onSetAvatarClick: (Long) -> Unit,
    viewModel: AlbumViewModel
) {
    val photos by viewModel.photos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部导航栏
        TopNavigationBar(
            onBackClick = onBackClick,
            onSaveClick = onSaveClick
        )
        
        // 相册内容
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (photos.isEmpty()) {
                // 空相册状态
                EmptyAlbumView(
                    onAddPhotoClick = onAddPhotoClick
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 添加照片按钮
                    item {
                        AddPhotoButton(
                            onClick = onAddPhotoClick
                        )
                    }
                    
                    // 照片列表
                    items(photos) { photo ->
                        PhotoItem(
                            photo = photo,
                            onDeleteClick = { onDeletePhotoClick(photo.id!!) },
                            onSetAvatarClick = { onSetAvatarClick(photo.id!!) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopNavigationBar(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 返回按钮
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.Black
            )
        }
        
        // 页面标题
        Text(
            text = "编辑相册",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        // 保存按钮
        TextButton(
            onClick = onSaveClick
        ) {
            Text(
                text = "保存",
                fontSize = 16.sp,
                color = Color(0xFF2196F3)
            )
        }
    }
}

@Composable
private fun EmptyAlbumView(
    onAddPhotoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 大号添加照片按钮
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    Color(0xFFF5F5F5),
                    RoundedCornerShape(16.dp)
                )
                .border(
                    2.dp,
                    Color(0xFFE0E0E0),
                    RoundedCornerShape(16.dp)
                )
                .clickable { onAddPhotoClick() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加照片",
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF999999)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "添加照片",
                    fontSize = 16.sp,
                    color = Color(0xFF666666)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 提示文字
        Text(
            text = "还没有照片",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "点击上方按钮添加第一张照片",
            fontSize = 14.sp,
            color = Color(0xFF666666)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 功能说明
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "相册功能说明",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "• 第一张照片将作为您的头像\n• 支持相机拍照和相册选择\n• 长按照片可进行管理操作",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun AddPhotoButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                Color(0xFFF5F5F5),
                RoundedCornerShape(8.dp)
            )
            .border(
                2.dp,
                Color(0xFFE0E0E0),
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "添加照片",
                modifier = Modifier.size(32.dp),
                tint = Color(0xFF999999)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "添加照片",
                fontSize = 12.sp,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun PhotoItem(
    photo: UserPhotoDTO,
    onDeleteClick: () -> Unit,
    onSetAvatarClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
    ) {
        // 照片
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_avatar_custom),
            contentDescription = "照片",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // 头像标识
        if (photo.isAvatar) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .background(
                        Color(0xFF4CAF50),
                        CircleShape
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "头像",
                    fontSize = 10.sp,
                    color = Color.White
                )
            }
        }
        
        // 更多选项按钮
        IconButton(
            onClick = { showMenu = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "更多选项",
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
        }
        
        // 下拉菜单
        if (showMenu) {
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                if (!photo.isAvatar) {
                    DropdownMenuItem(
                        text = { Text("设为头像") },
                        onClick = {
                            onSetAvatarClick()
                            showMenu = false
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text("删除") },
                    onClick = {
                        onDeleteClick()
                        showMenu = false
                    }
                )
            }
        }
    }
}
