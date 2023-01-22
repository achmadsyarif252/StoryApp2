package com.example.storyapp.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.adapter.StoryListAdapter
import com.example.storyapp.data.model.UserPreference
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.view.addstory.AddStoryActivity
import com.example.storyapp.view.login.LoginActivity
import com.example.storyapp.view.storymaps.StoryAppMaps
import com.example.storyapp.viewmodel.MainViewModel
import com.example.storyapp.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Setting")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(this, UserPreference.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> logoutDialog()
            R.id.lang_setting -> startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            R.id.maps_story -> startActivity(Intent(this@MainActivity, StoryAppMaps::class.java))
        }
        return true
    }

    private fun logoutDialog() {
        val dialogMessage = getString(R.string.logout_msg)
        val dialogTitle = getString(R.string.logout)


        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(dialogTitle)

        alertDialogBuilder
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                mainViewModel.logout()
                finish()
            }
            .setNegativeButton(getString(R.string.No)) { dialog, _ -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    private fun getData(token: String) {
        val adapter = StoryListAdapter()
        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
        binding.rvStories.setHasFixedSize(true)
        mainViewModel.story(token).observe(this) {
            adapter.submitData(lifecycle, it)
        }
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
    }

    private fun setupViewModel() {
        mainViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                TOKEN = user.token
                getData(user.token)
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    companion object {
        var TOKEN = ""
    }
}

