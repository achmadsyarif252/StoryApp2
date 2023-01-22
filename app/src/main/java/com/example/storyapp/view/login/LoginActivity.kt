package com.example.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.view.register.RegisterActivity
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.model.UserPreference
import com.example.storyapp.viewmodel.ViewModelFactory
import com.example.storyapp.view.main.MainActivity
import com.example.storyapp.viewmodel.LoginViewModel
import java.lang.ref.WeakReference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

lateinit var weakReference: WeakReference<ActivityLoginBinding>

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var user: UserModel
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.welcome_txt)

        weakReference = WeakReference(binding)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
    }


    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.getUser().observe(this) { user ->
            this.user = user
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    private fun setupAction() {
        binding.settingImageView.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        loginViewModel.msg.observe(this) {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.app_name))
                setMessage(it)
                setPositiveButton("OK") { _, _ ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        }


        binding.loginButton.setOnClickListener {
            val email = binding.loginemailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() -> {
                    binding.loginemailEditText.error = getString(R.string.fill_email)
                }
                password.isEmpty() -> {
                    binding.passwordEditText.error = getString(R.string.password_min_length)
                }

                else -> {
                    loginViewModel.authenticate(email, password)
                }
            }
        }
        binding.registerTv.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -40f, 40f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextview, View.ALPHA, 1f).setDuration(200)
        val msgTv = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(200)
        val emailTv = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(200)
        val emailEdl =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(200)
        val passwordTv =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(200)
        val passwordEdl =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(200)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(200)

        val registerInfo =
            ObjectAnimator.ofFloat(binding.registerInfo, View.ALPHA, 1f).setDuration(200)
        val register = ObjectAnimator.ofFloat(binding.registerTv, View.ALPHA, 1f).setDuration(200)

        val together = AnimatorSet().apply {
            playTogether(register, registerInfo)
        }

        AnimatorSet().apply {
            startDelay = 500
            playSequentially(
                title,
                msgTv,
                emailTv,
                emailEdl,
                passwordTv,
                passwordEdl,
                login,
                together
            )
            start()
        }
    }

    companion object {
        fun isErrorPassword(isError: Boolean) {
            val binding = weakReference.get()
            binding?.edLoginPassword?.isEndIconVisible = !isError
        }
    }
}