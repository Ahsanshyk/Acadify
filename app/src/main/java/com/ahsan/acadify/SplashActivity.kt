package com.ahsan.acadify

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        Handler().postDelayed(Runnable {
            checkUsers();
        }, 2000)

    }

    private fun checkUsers() {

        val firebaseUser = firebaseAuth.currentUser

        // No user logged in
        if (firebaseUser == null) {

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Reload user to get latest email verification status
        firebaseUser.reload().addOnSuccessListener {

            firebaseFirestore.collection("Users")
                .document(firebaseUser.uid)
                .get()
                .addOnSuccessListener { document ->

                    // Document missing
                    if (!document.exists()) {

                        firebaseAuth.signOut()

                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()

                        return@addOnSuccessListener
                    }

                    // Email not verified
                    if (!firebaseUser.isEmailVerified) {

                        startActivity(
                            Intent(
                                this,
                                VerifyEmailActivity::class.java
                            )
                        )

                        finishAffinity()

                    } else {

                        startActivity(
                            Intent(
                                this,
                                MainActivity::class.java
                            )
                        )

                        finish()
                    }
                }
                .addOnFailureListener {

                    firebaseAuth.signOut()

                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
        }
    }
}