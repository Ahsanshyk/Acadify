package com.ahsan.acadify

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.ahsan.acadify.databinding.ActivityVerifyEmailBinding

class VerifyEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyEmailBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Please wait")
        progressDialog!!.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        loadMyInfo()

        // Resend verification mail
        binding.sendMailBtn.setOnClickListener {

            val user = firebaseAuth.currentUser

            user?.sendEmailVerification()
                ?.addOnSuccessListener {

                    Toast.makeText(
                        this@VerifyEmailActivity,
                        "Verification Email has been sent. Please verify.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                ?.addOnFailureListener {

                    Log.d(
                        ContentValues.TAG,
                        "onFailure: Email not sent"
                    )
                }
        }

        // Continue button
        binding.continueBtn.setOnClickListener {

            checkEmailVerification()
        }

        // Logout button
        binding.logoutBtn.setOnClickListener {

            val builder = AlertDialog.Builder(this@VerifyEmailActivity)

            builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton(
                    "Yes"
                ) { dialogInterface, i ->

                    makeMeOffline()
                }
                .setNegativeButton(
                    "No"
                ) { dialogInterface, i ->

                    dialogInterface.dismiss()
                }
                .show()
        }
    }

    private fun loadMyInfo() {

        val documentReference: DocumentReference =
            firestore.collection("Users")
                .document(firebaseAuth.uid!!)

        documentReference.addSnapshotListener(
            this
        ) { ds, error ->

            if (ds != null && ds.exists()) {

                val email = ds.getString("email") ?: ""
                val name = ds.getString("name") ?: "User"
                val profileImage = ds.getString("profileImage") ?: ""

                binding.nameTv.text = name
                binding.emailTv.text = email

                try {

                    Picasso.get()
                        .load(profileImage)
                        .placeholder(R.drawable.ic_person_gray)
                        .into(binding.profileIv)

                } catch (e: Exception) {

                    binding.profileIv.setImageResource(R.drawable.ic_person_gray)
                }
            }
        }
    }

    private fun checkEmailVerification() {

        val user = firebaseAuth.currentUser

        user?.reload()?.addOnSuccessListener {

            if (true) {

                Toast.makeText(
                    this,
                    "Email Verified Successfully",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(
                        this@VerifyEmailActivity,
                        MainActivity::class.java
                    )
                )

                finish()

            } else {

                Toast.makeText(
                    this,
                    "Please verify your email first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun makeMeOffline() {

        progressDialog!!.setMessage("Logging out...")
        progressDialog!!.show()

        val hashMap = HashMap<String, Any>()
        hashMap["online"] = "false"

        val documentReference: DocumentReference =
            firestore.collection("Users")
                .document(firebaseAuth.uid!!)

        documentReference.update(hashMap)
            .addOnSuccessListener {

                progressDialog!!.dismiss()

                firebaseAuth.signOut()

                startActivity(
                    Intent(
                        this@VerifyEmailActivity,
                        LoginActivity::class.java
                    )
                )

                finish()
            }
            .addOnFailureListener { e ->

                progressDialog!!.dismiss()

                Toast.makeText(
                    this@VerifyEmailActivity,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}