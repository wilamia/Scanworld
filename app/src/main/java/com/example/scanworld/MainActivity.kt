package com.example.scanworld

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val requestCodeCamera = 101
    private val requestCodeStorage = 102
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        checkPermissions()

        findViewById<Button>(R.id.camera).setOnClickListener {
            startActivity(Intent(this, BarcodeScanner::class.java))
            overridePendingTransition(R.animator.no_animation_in, R.animator.no_animation_out)
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            startActivity(Intent(this, ImageScanner::class.java))
            overridePendingTransition(R.animator.no_animation_in, R.animator.no_animation_out)
        }

        findViewById<Button>(R.id.button3).setOnClickListener {
            startActivity(Intent(this, CreateBarcode::class.java))
            overridePendingTransition(R.animator.no_animation_in, R.animator.no_animation_out)
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        toolbar.setNavigationOnClickListener {
            drawerLayout.open()
            overridePendingTransition(R.animator.no_animation_in, R.animator.no_animation_out)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.main_page -> {
                    val intentMain = Intent(this, MainActivity::class.java)
                    startActivity(intentMain)
                    drawerLayout.close()
                    overridePendingTransition(
                        R.animator.no_animation_in,
                        R.animator.no_animation_out
                    )
                    true
                }

                R.id.about_us -> {
                    val intentAbout = Intent(this, AboutUs::class.java)
                    startActivity(intentAbout)
                    drawerLayout.close()
                    overridePendingTransition(
                        R.animator.no_animation_in,
                        R.animator.no_animation_out
                    )
                    true
                }

                R.id.instagram -> {
                    val instagramIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.instagram.com/your_instagram_account")
                    )
                    startActivity(instagramIntent)
                    drawerLayout.close()
                    overridePendingTransition(
                        R.animator.no_animation_in,
                        R.animator.no_animation_out
                    )
                    true
                }

                R.id.facebook -> {
                    val facebookIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.facebook.com/your_facebook_account")
                    )
                    startActivity(facebookIntent)
                    drawerLayout.close()
                    overridePendingTransition(
                        R.animator.no_animation_in,
                        R.animator.no_animation_out
                    )
                    true
                }

                R.id.phone_number -> {
                    val phoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+37500000000"))
                    startActivity(phoneIntent)
                    drawerLayout.close()
                    overridePendingTransition(
                        R.animator.no_animation_in,
                        R.animator.no_animation_out
                    )
                    true
                }

                R.id.email -> {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:scanworld@gmail.com") // только email
                    }
                    startActivity(emailIntent)
                    drawerLayout.close()
                    overridePendingTransition(
                        R.animator.no_animation_in,
                        R.animator.no_animation_out
                    )
                    true
                }

                else -> false
            }
        }
    }

    private fun checkPermissions() {
        if (!hasCameraPermission()) {
            requestCameraPermission()
        }
        if (!hasStoragePermissions()) {
            requestStoragePermissions()
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            requestCodeCamera
        )
    }

    private fun hasStoragePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                requestManageStoragePermission()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                requestCodeStorage
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestManageStoragePermission() {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, requestCodeStorage)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestCodeCamera -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permissions", "Camera permission granted")
                } else {
                    handlePermissionDenied(Manifest.permission.CAMERA)
                }
            }

            requestCodeStorage -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permissions", "Storage permission granted")
                } else {
                    handlePermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun handlePermissionDenied(permission: String) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            Toast.makeText(
                this,
                "Разрешение отклонено. Пожалуйста, предоставьте разрешение в настройках приложения.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                this,
                "Разрешение было отклонено. Вы можете изменить его в настройках.",
                Toast.LENGTH_LONG
            ).show()
            openAppSettings()
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
        overridePendingTransition(R.animator.no_animation_in, R.animator.no_animation_out)
    }
}