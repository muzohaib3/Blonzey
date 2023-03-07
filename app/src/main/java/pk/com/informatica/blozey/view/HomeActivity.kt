package pk.com.informatica.blozey.view

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import pk.com.informatica.blozey.R
import pk.com.informatica.blozey.databinding.ActivityHomeBinding
import pk.com.informatica.boschprofile.view.ktx.*

class HomeActivity : AppCompatActivity() {

    private lateinit var binding:ActivityHomeBinding
    private lateinit var webView: WebView
    companion object {
        const val MY_PERMISSIONS_REQUEST_CAMERA = 1001
        const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1002
    }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        webView()
        initViews()
    }

    private fun initViews()
    {
        supportActionBar?.hide()
        cameraPermission()
        permissionForExternalStorage()

        binding.webView.setOnLongClickListener { view ->
            try {
                showSaveImageDialog(view)
            }
            catch (e:Exception)
            {
                println("${e.message}")
            }

            true
        }
    }

    private fun webView()
    {
        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://www.blozey.com")
    }

    override fun onBackPressed()
    {
        binding.exitDialog.makeVisible()

        binding.btOK.click {
            var dialog = binding.exitDialog
            var myAnimation = AnimationUtils.loadAnimation(this, R.anim.animation)
            dialog.startAnimation(myAnimation)
            dialog.makeGone()
        }

        binding.btCancel.click {
            var dialog = binding.exitDialog
            var myAnimation = AnimationUtils.loadAnimation(this, R.anim.animation)
            dialog.startAnimation(myAnimation)
            dialog.makeGone()
        }

    }

    private fun cameraPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSIONS_REQUEST_CAMERA)
        }
        else
        {
            toast("You have not provided permission for camera")
        }
    }

    private fun permissionForExternalStorage()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
        } else
        {
            // Permission has already been granted
            // Write to external storage
        }
    }

    private fun showSaveImageDialog(view: View) {
        val hitTestResult = (view as WebView).hitTestResult
        if (hitTestResult.type == WebView.HitTestResult.IMAGE_TYPE ||
            hitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            val builder = AlertDialog.Builder(view.context)
            builder.setTitle("Save Image")
            builder.setMessage("Do you want to save this image?")
            builder.setPositiveButton("Yes") { dialog, which ->
                saveImage(hitTestResult.extra)
            }
            builder.setNegativeButton("No", null)
            builder.show()
        }
    }

    private fun saveImage(url: String?) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            .setAllowedOverRoaming(false)
            .setTitle("Downloading Image")
            .setDescription("Image download in progress")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${System.currentTimeMillis()}.jpg")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(this, "Image download started", Toast.LENGTH_SHORT).show()
    }
}