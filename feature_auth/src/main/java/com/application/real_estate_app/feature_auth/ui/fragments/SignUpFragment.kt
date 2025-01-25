package com.application.real_estate_app.feature_auth.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.application.real_estate_app.core.domain.models.UserType
import com.application.real_estate_app.core.common.events.LoginEvent
import com.application.real_estate_app.feature_auth.R
import com.application.real_estate_app.feature_auth.databinding.FragmentSignUpBinding
import com.application.real_estate_app.feature_auth.ui.viewModels.AuthViewModel
import com.application.real_estate_app.feature_auth.ui.views.VerificationCodeDialog
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private var selectedUserType: UserType? = null

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserTypeSpinner()

        binding.btnSignUp.setOnClickListener {
            val email = binding.emailInputLayout.editText?.text.toString().trim()
            val password = binding.passwordInputLayout.editText?.text.toString().trim()
            val phoneNumber = binding.phoneInputLayout.editText?.text.toString().trim()
            val userName = binding.userNameInputLayout.editText?.text.toString().trim()

            if (validateInputs(email, password, phoneNumber, userName)) {
                startPhoneVerification(phoneNumber) {
                    authViewModel.registerUser(email, password,
                        userName, phoneNumber, selectedUserType!!) { exception ->
                        showToast( getString(R.string.registration_failed, exception.message))
                    }
                }
            }
        }

        binding.AlreadyHaveAccount.setOnClickListener {
            val navController = findNavController()

            // Pop the SignUpFragment off the backstack and navigate to LogInFragment
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.signUpFragment, inclusive = true) // Remove SignUpFragment from the backstack
                .setLaunchSingleTop(true) // Avoid multiple instances of LogInFragment
                .build()

            navController.navigate(R.id.loginFragment, null, navOptions)
        }


        observeViewModel()
    }

    private fun setupUserTypeSpinner() {
        val userTypes = UserType.entries.map { it.displayName }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf(R.string.select_user_type) + userTypes // Add a default option
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.userTypeSpinner.adapter = adapter

        binding.userTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedUserType = if (position > 0) {
                    UserType.fromDisplayName(userTypes[position - 1]) // Adjust index for default option
                } else {
                    null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedUserType = null
            }
        }
    }

    private fun validateInputs(
        email: String, password: String, phoneNumber: String,
        userName: String
    ): Boolean {
        return when {
            email.isBlank() || password.isBlank() || phoneNumber.isBlank() || userName.isBlank() -> {
                showToast(getString(R.string.fill_in_all_fields))
                false
            }
            selectedUserType == null -> {
                showToast(getString(R.string.invalid_user_type_selected))
                false
            }
            else -> true
        }
    }

    private fun startPhoneVerification(phoneNumber: String, onSuccess: () -> Unit) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                authViewModel.signInWithPhoneAuthCredential(credential)
                onSuccess()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                showToast(getString(R.string.verification_failed, e.message))
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                showToast(getString(R.string.code_sent_to, phoneNumber))
                // Show the verification code dialog
                VerificationCodeDialog(
                    context = requireContext(),
                    verificationId = verificationId,
                    resendingToken = token,
                    phoneNumber = phoneNumber
                ).show()
            }
        }
        authViewModel.startPhoneNumberVerification(phoneNumber, callbacks)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.phoneVerificationState.collect { state ->
                    when (state) {
                        is AuthViewModel.VerificationState.Success -> {
                            EventBus.getDefault().post(LoginEvent()) // Trigger the login event
                        }
                        is AuthViewModel.VerificationState.Error -> {
                            showToast(state.message)
                        }
                        else -> {
                            // Handle idle or other states if needed
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
