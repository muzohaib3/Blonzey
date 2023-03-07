package pk.com.informatica.blozey

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import pk.com.informatica.blozey.view.HomeActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        Handler(mainLooper).postDelayed({splash()},3000)
    }

    private fun splash()
    {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}