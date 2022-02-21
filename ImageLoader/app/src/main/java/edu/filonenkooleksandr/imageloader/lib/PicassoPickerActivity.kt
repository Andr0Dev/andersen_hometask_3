package edu.filonenkooleksandr.imageloader.lib

import android.Manifest
import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.squareup.picasso.Picasso
import edu.filonenkooleksandr.imageloader.R
import edu.filonenkooleksandr.imageloader.databinding.ActivityPicassoPickerBinding
import edu.filonenkooleksandr.imageloader.standard.StandardActivity
import java.io.File

class PicassoPickerActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityPicassoPickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPicassoPickerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            askStoragePermissions()
        } else {
            viewBinding.imageSize.doAfterTextChanged {
                downloadImage()
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    fun askStoragePermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Permission required")
                    .setMessage("Permission required to save photos from the Web.")
                    .setPositiveButton("Allow") { dialog, id ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            REQUEST_WRITE_EXTERNAL_STORAGE
                        )
                    }
                    .setNegativeButton("Deny") { dialog, id -> dialog.cancel() }
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    downloadImage()
                } else {
                    Toast.makeText(
                        this,
                        "You denied saving permission",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                return
            }
        }
    }

    private fun downloadImage() {
        Picasso
            .get()
            .load(url.plus(viewBinding.imageSize.text.toString()))
            .into(viewBinding.image)
    }

    private fun statusMessage(url: String, directory: File, status: Int): String =
        when (status) {
            DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL ->
                "Image downloaded successfully in $directory" + File.separator + url.substring(
                    url.lastIndexOf("/") + 1
                )
            else -> "There's nothing to download"
        }

    companion object {
        val url = "https://picsum.photos/id/"
        private const val REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }
}