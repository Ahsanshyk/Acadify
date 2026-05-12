package com.ahsan.acadify

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ahsan.acadify.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

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

        // Move to RegisterActivity
        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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
        password = binding.passwordEt.text.toString().trim()

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

        val documentReference: DocumentReference =
            firebaseFirestore.collection("Users")
                .document(firebaseAuth.uid!!)

        documentReference.update(hashMap)
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
            .addOnSuccessListener { ds ->

                binding.progressBar.visibility = View.GONE

                val userType = ds.getString("userType")

                if (!firebaseUser.isEmailVerified) {

                    startActivity(
                        Intent(
                            this@LoginActivity,
                            VerifyEmailActivity::class.java
                        )
                    )

                    finishAffinity()

                } else {

                    when (userType) {

                        "teachers" -> {
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    MainActivity::class.java
                                )
                            )
                            finish()
                        }

                        "user" -> {
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    MainActivity::class.java
                                )
                            )
                            finish()
                        }

                        "anurag" -> {
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    MainActivity::class.java
                                )
                            )
                            finish()
                        }

                        else -> {

                            Toast.makeText(
                                this,
                                "Unknown User Type",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            }
            .addOnFailureListener { e ->

                binding.progressBar.visibility = View.GONE

                Toast.makeText(
                    this,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}