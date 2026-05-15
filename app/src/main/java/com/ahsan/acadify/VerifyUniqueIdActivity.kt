package com.ahsan.acadify

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ahsan.acadify.databinding.ActivityVerifyUniqueIdBinding

class VerifyUniqueIdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyUniqueIdBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    private var userType: String? = ""
    private var uniqueId: String? = ""

    private var isRegistered = false
    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerifyUniqueIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        // Disable fields initially
        binding.phoneEt.isEnabled = false
        binding.sendOtpBtn.isEnabled = false

        // Help Button
        binding.helpBtn.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    NeedHelpActivity::class.java
                )
            )
        }

        // Back Button
        binding.backBtn.setOnClickListener {

            onBackPressed()
        }

        // Verify Unique ID Button
        binding.continueBtn.setOnClickListener {

            validateData()
        }

        // Send OTP Button
        binding.sendOtpBtn.setOnClickListener {

            validatePhoneNumber()
        }
    }

    private fun validateData() {

        uniqueId = binding.uniqueIdEt.text.toString().trim()

        if (TextUtils.isEmpty(uniqueId)) {

            Toast.makeText(
                this,
                "Enter Your Unique Id....!",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            checkExistingUniqueId()
        }
    }

    private fun checkExistingUniqueId() {

        binding.progressBar.visibility = View.VISIBLE

        val documentReference =
            firebaseFirestore.collection("RegisteredUniqueId")
                .document(uniqueId!!)

        documentReference.get()
            .addOnSuccessListener { document ->

                isRegistered = document.exists()

                if (isRegistered) {

                    binding.progressBar.visibility = View.GONE

                    Toast.makeText(
                        this@VerifyUniqueIdActivity,
                        "This Unique ID is already Registered...!!",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    verifyUniqueId()
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

    private fun verifyUniqueId() {

        val studentId = binding.uniqueIdEt.text.toString().trim()

        firebaseFirestore.collection("StudentIds")
            .document(studentId)
            .get()
            .addOnSuccessListener { document ->

                binding.progressBar.visibility = View.GONE

                if (document.exists()) {

                    val phone = document.getString("phone")

                    // IMPORTANT FIX
                    userType = document.getString("userType")

                    binding.phoneEt.setText(phone)

                    Toast.makeText(
                        this,
                        "Student ID Verified",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Enable OTP Button
                    binding.sendOtpBtn.isEnabled = true

                } else {

                    Toast.makeText(
                        this,
                        "Unique ID not found",
                        Toast.LENGTH_SHORT
                    ).show()
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

    private fun validatePhoneNumber() {

        phoneNumber = binding.phoneEt.text.toString().trim()

        if (TextUtils.isEmpty(phoneNumber)) {

            Toast.makeText(
                this,
                "Phone Number not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val intent = Intent(
            this@VerifyUniqueIdActivity,
            OTPActivity::class.java
        )

        intent.putExtra(
            "phoneNumber",
            phoneNumber
        )

        intent.putExtra(
            "uniqueId",
            binding.uniqueIdEt.text.toString().trim()
        )

        intent.putExtra(
            "userType",
            userType
        )

        startActivity(intent)
    }
}