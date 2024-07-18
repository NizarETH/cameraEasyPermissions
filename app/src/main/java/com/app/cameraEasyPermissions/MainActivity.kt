package com.app.cameraEasyPermissions
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        const val REQUEST_CODE_CAMERA_PERMISSION = 123
    }
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri


    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {

            val imageView = findViewById<ImageView>(R.id.image)
            imageView.setImageURI(photoUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.camera_button).setOnClickListener {
            openCameraWithPermission()
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_CAMERA_PERMISSION)
    private fun openCameraWithPermission() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            openCamera()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your camera to take photos.",
                REQUEST_CODE_CAMERA_PERMISSION,
                Manifest.permission.CAMERA
            )
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(this, "com.app.cameraEasyPermissions.provider", photoFile)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        cameraLauncher.launch(cameraIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        openCamera()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        // Permission was denied, show a message to the user
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Créer un nom de fichier d'image
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Enregistrer un chemin de fichier : chemin pour l'utiliser avec les intentions ACTION_VIEW
            var currentPhotoPath = absolutePath
        }
    }
}
