package com.example.buynow.presentation.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.buynow.R
import com.example.buynow.utils.Extensions.toast
import com.example.buynow.utils.FirebaseUtils.firebaseAuth
import com.example.buynow.presentation.LoadingDialog
import com.example.buynow.utils.FirebaseUtils

class LoginActivity : AppCompatActivity() {

    lateinit var signInEmail: String
    lateinit var signInPassword: String
    lateinit var signInBtn: Button
    lateinit var emailEt: EditText
    lateinit var passEt: EditText

    lateinit var loadingDialog: LoadingDialog

    lateinit var emailError: TextView
    lateinit var passwordError: TextView
    lateinit var forgottenPassTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signUpTv = findViewById<TextView>(R.id.signUpTv)
        signInBtn = findViewById(R.id.loginBtn)
        emailEt = findViewById(R.id.emailEt)
        passEt = findViewById(R.id.PassEt)
        emailError = findViewById(R.id.emailError)
        passwordError = findViewById(R.id.passwordError)
        forgottenPassTv = findViewById(R.id.forgottenPassTv)

        textAutoCheck()

        loadingDialog = LoadingDialog(this)

        signUpTv.setOnClickListener {
            intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        signInBtn.setOnClickListener {
            checkInput()
        }

        forgottenPassTv.setOnClickListener {
            resetPassword()
        }
    }

    private fun textAutoCheck() {
        emailEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (emailEt.text.isEmpty()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                } else if (Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext, R.drawable.ic_check), null)
                    emailError.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext, R.drawable.ic_check), null)
                    emailError.visibility = View.GONE
                }
            }
        })

        passEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (passEt.text.isEmpty()) {
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                } else if (passEt.text.length > 4) {
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext, R.drawable.ic_check), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                passwordError.visibility = View.GONE
                if (count > 4) {
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext, R.drawable.ic_check), null)
                }
            }
        })
    }

    private fun checkInput() {
        if (emailEt.text.isEmpty()) {
            emailError.visibility = View.VISIBLE
            emailError.text = "Email Can't be Empty"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
            emailError.visibility = View.VISIBLE
            emailError.text = "Enter Valid Email"
            return
        }
        if (passEt.text.isEmpty()) {
            passwordError.visibility = View.VISIBLE
            passwordError.text = "Password Can't be Empty"
            return
        }
        signInUser()
    }

    private fun signInUser() {
        loadingDialog.startLoadingDialog()
        signInEmail = emailEt.text.toString().trim()
        signInPassword = passEt.text.toString().trim()
        firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
            .addOnCompleteListener { signIn ->
                if (signIn.isSuccessful) {
                    loadingDialog.dismissDialog()
                    startActivity(Intent(this, HomeActivity::class.java))
                    toast("Signed in successfully")
                    finish()
                } else {
                    toast("Sign in failed")
                    loadingDialog.dismissDialog()
                }
            }
    }

    private fun resetPassword() {
        val email = emailEt.text.toString().trim()

        if (email.isEmpty()) {
            emailError.visibility = View.VISIBLE
            emailError.text = "Email Can't be Empty"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError.visibility = View.VISIBLE
            emailError.text = "Enter Valid Email"
            return
        }

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                toast("Password reset email sent successfully")
            }
            .addOnFailureListener { e ->
                toast("Failed to send password reset email: ${e.message}")
            }
    }
}
