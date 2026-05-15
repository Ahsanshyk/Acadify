package com.ahsan.acadify

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ahsan.acadify.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        // Move to Unique ID Verification
        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this, VerifyUniqueIdActivity::class.java))
        }

        // Forgot Password
        binding.forgotTv.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        // Need Help
        binding.needHelpBtn.setOnClickListener {
            startActivity(Intent(this, NeedHelpActivity::class.java))
        }

        // Login Button
        binding.loginBtn.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {

        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            Toast.makeText(
                this,
                "Invalid Email Format",
                Toast.LENGTH_SHORT
            ).show()

        } else if (TextUtils.isEmpty(password)) {

            Toast.makeText(
                this,
                "Enter Your Password",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            loginUser()
        }
    }

    private fun loginUser() {

        binding.progressBar.visibility = View.VISIBLE

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                makeMeOnline()

            }
            .addOnFailureListener { e ->

                binding.progressBar.visibility = View.GONE

                Toast.makeText(
                    this,
                    "Login Failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun makeMeOnline() {

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["online"] = "true"

        val documentReference =
            firebaseFirestore.collection("Users")
                .document(firebaseAuth.uid!!)

        documentReference.set(hashMap, SetOptions.merge())
            .addOnSuccessListener {

                checkUser()

            }
            .addOnFailureListener { e ->

                binding.progressBar.visibility = View.GONE

                Toast.makeText(
                    this@LoginActivity,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun checkUser() {

        val firebaseUser = firebaseAuth.currentUser

        val documentReference =
            firebaseFirestore.collection("Users")
                .document(firebaseUser!!.uid)

        documentReference.get()
            .addOnSuccessListener {

                binding.progressBar.visibility = View.GONE

                startActivity(
                    Intent(
                        this@LoginActivity,
                        MainActivity::class.java
                    )
                )

                finish()
            }
            .addOnFailureListener { e ->

                binding.progressBar.visibility = View.GONE

                Toast.makeText(
                    this,
                    "Error Code: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()

                Log.e("LOGIN_ERROR", e.message.toString())
            }
    }
}