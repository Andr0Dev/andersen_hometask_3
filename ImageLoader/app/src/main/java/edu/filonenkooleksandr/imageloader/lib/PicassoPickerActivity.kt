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
                    .setTitle(getString(R.string.permission_required))
                    .setMessage(getString(R.string.message))
                    .setPositiveButton(getString(R.string.positive_button_title)) { dialog, id ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            REQUEST_WRITE_EXTERNAL_STORAGE
                        )
                    }
                    .setNegativeButton(getString(R.string.negative_button_title)) { dialog, id ->
                        dialog.cancel()
                    }
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
                        getString(R.string.toast_message),
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
            DownloadManager.STATUS_FAILED -> getString(R.string.status_failed)
            DownloadManager.STATUS_PAUSED -> getString(R.string.status_paused)
            DownloadManager.STATUS_PENDING -> getString(R.string.status_pending)
            DownloadManager.STATUS_RUNNING -> getString(R.string.running)
            DownloadManager.STATUS_SUCCESSFUL ->
                "Image downloaded successfully in $directory" + File.separator + url.substring(
                    url.lastIndexOf("/") + 1
                )
            else -> getString(R.string.else_variant)
        }

    companion object {
        val url = "https://picsum.photos/id/"
        private const val REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }
}