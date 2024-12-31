package com.application.real_estate_app.feature_auth.ui.views

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.application.real_estate_app.feature_auth.R
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class VerificationCodeDialog(
    context: Context,
    private val verificationId: String,
    private val phoneNumber: String,
    private val resendingToken: PhoneAuthProvider.ForceResendingToken? = null
) : Dialog(context) {

    private lateinit var editTextVerificationCode: EditText
    private lateinit var btnVerifyCode: Button
    private lateinit var btnResendCode: Button
    private lateinit var tvCountDownTimer: MaterialTextView
    private lateinit var phoneNumberTextView: MaterialTextView
    private lateinit var countDownTimer: CountDownTimer


    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verification_code_dialog)

        editTextVerificationCode = findViewById(R.id.VerificationCode)
        btnVerifyCode = findViewById(R.id.btnVerifyCode)
        btnResendCode = findViewById(R.id.btnResendCode)
        tvCountDownTimer = findViewById(R.id.countdownTimer)
        phoneNumberTextView = findViewById(R.id.phoneNumber)

        phoneNumberTextView.text = context.getString(R.string.verification_code_sent, phoneNumber)


        // Initially hide the Resend button
        btnResendCode.visibility = Button.GONE

        // Set up countdown timer for 2 minutes (120 seconds)
        startCountdownTimer()

        btnVerifyCode.setOnClickListener {
            val code = editTextVerificationCode.text.toString().trim()
            if (code.isEmpty() || code.length != 6) {
                Toast.makeText(context, context.getString(R.string.please_enter_valid_code), Toast.LENGTH_SHORT).show()
            } else {
                btnVerifyCode.isEnabled = false // Disable button to prevent multiple clicks
                verifyPhoneNumberWithCode(code)
            }
        }

        // Set a handler to show the Resend button after 2 minutes (120 seconds)
        Handler(Looper.getMainLooper()).postDelayed({
            btnResendCode.visibility = Button.VISIBLE // Show the button after the delay
        }, TimeUnit.MINUTES.toMillis(2)) // 2 minutes timeout

        // Set click listener for Resend button
        btnResendCode.setOnClickListener {
            resendVerificationCode(phoneNumber)
        }
    }


    private fun startCountdownTimer() {
        countDownTimer = object : CountDownTimer(TimeUnit.MINUTES.toMillis(2), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                tvCountDownTimer.text = context.getString(R.string.code_expires_in, minutes, seconds)
            }

            override fun onFinish() {
                tvCountDownTimer.text = context.getString(R.string.code_expired)
                btnResendCode.visibility = Button.VISIBLE // Show the button after the timer expires
            }
        }
        countDownTimer.start()
    }

    private fun verifyPhoneNumberWithCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                btnVerifyCode.isEnabled = true // Re-enable button
                if (task.isSuccessful) {
                    Toast.makeText(context, context.getString(R.string.verification_auto_completed), Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    Toast.makeText(context, context.getString(R.string.verification_failed, task.exception?.message), Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun resendVerificationCode(phoneNumber: String) {
        if (resendingToken != null) {
            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(context as? Activity ?: return)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        Toast.makeText(context, context.getString(R.string.phone_verified_successfully), Toast.LENGTH_SHORT).show()
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        Toast.makeText(context, context.getString(R.string.resend_failed, e.message), Toast.LENGTH_LONG).show()
                    }

                    override fun onCodeSent(newVerificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                        Toast.makeText(context, context.getString(R.string.code_resent_successfully), Toast.LENGTH_SHORT).show()
                    }
                })
                .setForceResendingToken(resendingToken)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        } else {
            Toast.makeText(context, context.getString(R.string.unable_to_resend), Toast.LENGTH_LONG).show()
        }
    }

    override fun dismiss() {
        super.dismiss()
        // Cancel the countdown timer if the dialog is dismissed
        countDownTimer.cancel()
    }
}

