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
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        val splashScreen = installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupSplashExitAnimation(splashScreen)
        prepareSound()
        supabaseGetClient()
    }

//    private fun getData() {
//        lifecycleScope.launch {
//            val client = supabaseGetClient()
//            val clientSupa = client.postgrest["users"].select()
//            clientSupa.decodeList<User>()
//            Log.d("SUPABASE", "data list : ${clientSupa.data}")
//        }
//    }

    private fun supabaseGetClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://hwihalqedwjglqnvjxnc.supabase.co",
            supabaseKey = "sb_publishable_c2uplatUZhOdaq-fVZWt1w_V-K12NsW"
        ) {
            install(Postgrest)
        }
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        WindowInsetsControllerCompat(window, binding.root).apply {
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.navigationBars())
        }
    }

    private fun setupSplashExitAnimation(splashScreen: androidx.core.splashscreen.SplashScreen) {
        splashScreen.setOnExitAnimationListener { splashView ->
            splashView.view.animate()
                .alpha(0f)
                .setDuration(800L)
                .setInterpolator(AccelerateInterpolator())
                .withEndAction {
                    splashView.remove()
                    stopAndReleaseSound()
                }
                .start()
        }
    }

    private fun prepareSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.eating)
        mediaPlayer?.start()
    }

    private fun stopAndReleaseSound() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }

    override fun onStop() {
        super.onStop()
        stopAndReleaseSound() // lebih aman mengikuti lifecycle
    }
}
