package com.example.storyapp.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.data.model.UserPreference
import com.example.storyapp.viewmodel.ViewModelFactory
import com.example.storyapp.view.login.LoginActivity
import com.example.storyapp.viewmodel.RegisterViewModel
import java.lang.ref.WeakReference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


//untuk akses ke view dan menghindari memorileak
var weakReference: WeakReference<ActivityRegisterBinding>? = null

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.register_txt)

        weakReference = WeakReference(binding)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()

        binding.loginTv.setOnClickListener(this)

    }

    fun successLoginAlert() {
        AlertDialog.Builder(this@RegisterActivity).apply {
            setTitle("Yeah!")
            setMessage(
                R.string.register_result_msg
            )
            setPositiveButton("OK") { _, _ ->
                finish()
            }
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(200)
        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(200)
        val nameEdt =
            ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(200)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(200)
        val emailEdt =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(200)
        val password =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(200)
        val passwordEdt =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(200)
        val signUp = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(200)

        val loginText = ObjectAnimator.ofFloat(binding.loginGuide, View.ALPHA, 1f).setDuration(200)
        val login = ObjectAnimator.ofFloat(binding.loginTv, View.ALPHA, 1f).setDuration(200)

        val together = AnimatorSet().apply {
            playTogether(loginText, login)
        }

        AnimatorSet().apply {
            startDelay = 500
            playSequentially(
                title,
                name,
                nameEdt,
                email,
                emailEdt,
                password,
                passwordEdt,
                signUp,
                together
            )
            start()
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.loginTv -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    private fun setupAction() {
        registerViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        registerViewModel.isError.observe(this) {
            isError = it
        }

        registerViewModel.alertMessage.observe(this) {
            AlertDialog.Builder(this@RegisterActivity).apply {
                setTitle(if (isError) "Error" else "Yeah!")
                setMessage(it)
                setPositiveButton("OK") { _, _ ->
                    if (!isError) finish()
                }
                create()
                show()
            }
        }
        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                name.isEmpty() -> {
                    binding.edRegisterName.error = getString(R.string.fill_name)
                }
                email.isEmpty() -> {
                    binding.edRegisterEmail.error = getString(R.string.fill_email)
                }
                password.isEmpty() -> {
                    binding.edRegisterPassword.error = getString(R.string.fill_password)
                }
                else -> {
                    registerViewModel.registerUser(name, email, password)
                    registerViewModel.isError.observe(this) {
                        if (!it) successLoginAlert()
                    }
                }
            }
        }

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
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, UserPreference.getInstance(dataStore))
        )[RegisterViewModel::class.java]
    }

    companion object {
        var isError = false

        fun isErrorPassword(isError: Boolean) {
            val binding = weakReference?.get()
            binding?.edRegisterPassword?.isEndIconVisible = !isError
        }

    }
}
