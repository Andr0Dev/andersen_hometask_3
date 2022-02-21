package edu.filonenkooleksandr.imageloader.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.filonenkooleksandr.imageloader.R
import edu.filonenkooleksandr.imageloader.databinding.ActivityMainBinding
import edu.filonenkooleksandr.imageloader.lib.PicassoPickerActivity
import edu.filonenkooleksandr.imageloader.standard.StandardActivity

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.withPicasso.setOnClickListener {
            startActivity(Intent(this, PicassoPickerActivity::class.java))
        }

        viewBinding.standardDownload.setOnClickListener {
            startActivity(Intent(this, StandardActivity::class.java))
        }
    }
}