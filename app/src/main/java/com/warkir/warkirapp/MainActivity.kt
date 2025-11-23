package com.warkir.warkirapp

import android.media.MediaPlayer
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.warkir.warkirapp.databinding.ActivityMainBinding

//Activity Utama
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        viewCompat()

        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.eating)
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val splashScreen = installSplashScreen()
        setContentView(binding.root)
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            splashScreenView.view
                .animate()
                .alpha(0f)
                .setDuration(1000L)
                .setInterpolator(
                    AccelerateInterpolator()
                ).withEndAction {
                    splashScreenView.remove()
                    try {
                        if (mediaPlayer?.isPlaying == true) {
                            mediaPlayer?.stop()
                        }
                        mediaPlayer?.release()
                        mediaPlayer = null
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /*
    * Fungsi ini mengatur bagaimana tampilan menyesuaikan diri agar tidak ketutupan status bar atau navigation bar.
    * Google mendorong desain Immersive UI / Edge-to-Edge:
      UI boleh sampai tepi layar untuk kesan luas dan modern, tapi tetap harus aman untuk konten.
    *
    * */
    private fun viewCompat() =
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
}