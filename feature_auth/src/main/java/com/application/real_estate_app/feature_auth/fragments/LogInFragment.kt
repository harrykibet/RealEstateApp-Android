package com.application.real_estate_app.feature_auth.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.application.real_estate_app.core.events.LoginEvent
import com.application.real_estate_app.feature_auth.R
import com.application.real_estate_app.feature_auth.databinding.FragmentLogInBinding
import com.application.real_estate_app.feature_auth.viewModels.AuthViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class LogInFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels()
    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle back press to exit the app
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Show a confirmation dialog or directly exit the app
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Exit App")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss() // Close the dialog and do nothing
                    }
                    .setPositiveButton("Exit") { _, _ ->
                        requireActivity().finish() // Exit the app
                    }
                    .show()
            }
        })

        // If user is already logged in, navigate to home fragment
        if (authViewModel.isUserLoggedIn()) {
            EventBus.getDefault().post(LoginEvent()) // Trigger the login event
            return
        }

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.signUpButton.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(
                    R.id.loginFragment,
                    inclusive = false
                ) // Pop back to LoginFragment without removing it
                .setLaunchSingleTop(true) // Avoid multiple instances
                .build()

            findNavController().navigate(
                R.id.action_loginFragment_to_signUpFragment,
                null,
                navOptions
            )
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailField.text.toString()
            val password = binding.passwordField.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()){
                loginUser(email, password)
            }
            else {
                // Show a SnackBar for the user to fill all the fields
                Snackbar.make(requireView(), "Please fill out the required fields", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.btnGoogleSignIn.setOnClickListener {
            authViewModel.signInWithGoogle(requireActivity())
        }

        binding.PasswordReset.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun observeViewModel() {
        authViewModel.googleSignInResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                EventBus.getDefault().post(LoginEvent()) // Trigger the login event
            }.onFailure {
                Toast.makeText(
                    requireContext(),
                    "Sign-In Failed: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.resetPasswordStatus.collect { result ->
                    result.onSuccess {
                        Toast.makeText(
                            requireContext(),
                            "Password reset email sent!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.onFailure {
                        Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        authViewModel.loginUser(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                EventBus.getDefault().post(LoginEvent()) // Trigger the login event
            } else {
                Toast.makeText(
                    requireContext(),
                    "Login failed: ${task.exception?.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showForgotPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.forgot_password_dialog, null)
        val emailEditText = dialogView.findViewById<android.widget.EditText>(R.id.emailEditText)
        val sendResetButton = dialogView.findViewById<android.widget.Button>(R.id.sendResetButton)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        sendResetButton.setOnClickListener {
            val email = emailEditText.text.toString()
            if (email.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        sendPasswordResetEmail(email)
                        Log.d("PasswordReset", "Password reset email sent!")
                    } catch (e: Exception) {
                        Log.e("PasswordReset", "Error sending password reset email: ${e.message}")
                    }
                }
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please enter a valid email", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        dialog.show()
    }

    private suspend fun sendPasswordResetEmail(email: String) {
        if (email.isNotEmpty()) {
            authViewModel.resetPassword(email)
        } else {
            Toast.makeText(
                requireContext(),
                "Please enter a valid email address",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == authViewModel.requestCode) {
            authViewModel.handleGoogleSignInResult(data)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
