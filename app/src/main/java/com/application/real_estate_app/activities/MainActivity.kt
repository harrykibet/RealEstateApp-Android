package com.application.real_estate_app.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.application.real_estate_app.R
import com.application.real_estate_app.feature_auth.viewModels.AuthViewModel
import com.application.real_estate_app.feature_profile.viewmodels.ProfileViewModel
import com.application.real_estate_app.utilities.FireStoreConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private val authViewModel: AuthViewModel by viewModels() // ViewModel for checking authentication


    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Install the splash screen before calling super.onCreate
            installSplashScreen()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainactivity)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        // Initialize the NavController
        navController = navHostFragment.navController

        // Check if authentication status is passed in the intent for Android 11 and below
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            val isAuthenticated = intent.getBooleanExtra("USER_AUTHENTICATED", false)
            navigateBasedOnAuthentication(isAuthenticated)
        }

        authViewModel.checkAuthentication() // Trigger the authentication check

        // Keep the splash screen visible until authentication check completes
        val content = findViewById<View>(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // Check if the authentication check is complete
                return if (authViewModel.isAuthCheckComplete()) {
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    authViewModel.isUserLoggedIn.value?.let { isAuthenticated ->
                        Toast.makeText(this@MainActivity, "Authentication Status: $isAuthenticated", Toast.LENGTH_SHORT).show()
                        navigateBasedOnAuthentication(isAuthenticated)
                    }
                    true // Proceed with normal rendering
                } else {
                    false // Hold off on drawing the UI
                }
            }
        })

        // Observe authentication status
        authViewModel.isUserLoggedIn.observe(this) { isAuthenticated ->
            navigateBasedOnAuthentication(isAuthenticated)
        }

        // Initialize FireStore settings
        FireStoreConfig.initFireStoreSettings()
    }

    private fun navigateBasedOnAuthentication(isAuthenticated: Boolean) {
        if (isAuthenticated) {
            showHomeFragment()
        } else {
            showLoginFragment()
        }
    }

    private fun showHomeFragment() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        // Set bottom navigation visibility
        bottomNavigationView.visibility = View.VISIBLE
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        setupBottomNavigation()

        // Navigate to the home fragment and clear login fragment from the back stack
        navController.popBackStack(com.application.real_estate_app.feature_auth.R.id.feature_auth_nav_graph, true)
        navController.navigate(com.application.real_estate_app.feature_home.R.id.feature_home_nav_graph)
    }

    private fun showLoginFragment() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.visibility = View.GONE // Hide BottomNavigationView

        //Clear all the previous fragments from the back stack
        navController.popBackStack(com.application.real_estate_app.feature_home.R.id.feature_home_nav_graph, true)
        navController.popBackStack(com.application.real_estate_app.feature_profile.R.id.feature_profile_nav_graph, true)
        navController.popBackStack(com.application.real_estate_app.feature_explore.R.id.feature_explore_nav_graph, true)
        navController.popBackStack(com.application.real_estate_app.feature_property.R.id.feature_property_nav_graph, true)
        navController.popBackStack(com.application.real_estate_app.feature_home.R.id.feature_favorite_nav_graph, true)

        // Navigate to the login fragment
        navController.navigate(com.application.real_estate_app.feature_auth.R.id.feature_auth_nav_graph)
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(com.application.real_estate_app.feature_home.R.id.feature_home_nav_graph)
                    true
                }

                R.id.mapsFragment -> {
                    navController.navigate(com.application.real_estate_app.feature_explore.R.id.feature_explore_nav_graph)
                    true
                }

                R.id.addPropertyFragment -> {
                    navController.navigate(com.application.real_estate_app.feature_property.R.id.feature_property_nav_graph)
                    true
                }

                R.id.favoritesFragment -> {
                    navController.navigate(com.application.real_estate_app.feature_home.R.id.feature_favorite_nav_graph)
                    true
                }

                R.id.profileFragment -> {
                    navController.navigate(com.application.real_estate_app.feature_profile.R.id.feature_profile_nav_graph)
                    true
                }

                else -> false
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            if (!navController.popBackStack()) {
                finish()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        navController.handleDeepLink(intent)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)  // Register to listen for events
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)  // Unregister when the activity stops
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLogoutEvent(event: ProfileViewModel.LogoutEvent) {
        showLoginFragment()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginEvent(event: AuthViewModel.LoginEvent) {
        showHomeFragment()
    }
}
