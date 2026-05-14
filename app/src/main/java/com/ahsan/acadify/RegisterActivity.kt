package com.ahsan.acadify
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ahsan.acadify.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var uniqueId = ""
    private var userType = "user"
    private var phoneNumber = ""
    private var fullName = ""
    private var email = ""
    private var password = ""
    private var confirmPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        uniqueId = intent.getStringExtra("uniqueId") ?: ""
        phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
        userType = intent.getStringExtra("userType") ?: "user"

        binding.registerBtn.setOnClickListener {
            validateData()
        }

    }

    private fun validateData() {
        fullName = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString()
        confirmPassword = binding.cPasswordEt.text.toString().trim()

        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Enter Name....", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email....", Toast.LENGTH_SHORT).show()
            return
        }
        if (password!!.length < 6) {
            Toast.makeText(
                this,
                "Password must be atleast 6 character long....",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Password doesn't match....", Toast.LENGTH_SHORT).show()
            return
        }
        createUserAccount()
    }

    private fun createUserAccount() {

        binding.progressBar.visibility = View.VISIBLE

        firebaseAuth.createUserWithEmailAndPassword(email!!, password!!)
            .addOnSuccessListener {

                val user = firebaseAuth.currentUser

                user?.sendEmailVerification()

                Toast.makeText(
                    this,
                    "Registration Successful. Verify Email.",
                    Toast.LENGTH_SHORT
                ).show()

                saveFirebaseData()
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

    private fun saveFirebaseData() {

        binding.progressBar.visibility = View.VISIBLE

        val uid = firebaseAuth.uid ?: return

        val userMap = hashMapOf<String, Any>(
            "uid" to uid,
            "name" to fullName,
            "email" to email,
            "userType" to "user",
            "online" to "true",
            "profileImage" to "",
            "phone" to "",
            "uniqueId" to "",
            "timestamp" to System.currentTimeMillis().toString()
        )

        firebaseFirestore.collection("Users")
            .document(uid)
            .set(userMap)
            .addOnSuccessListener {

                binding.progressBar.visibility = View.GONE

                Toast.makeText(
                    this,
                    "Data Saved Successfully",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    )
                )

                finish()
            }
            .addOnFailureListener { e ->

                binding.progressBar.visibility = View.GONE

                Toast.makeText(
                    this,
                    "Firestore Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

                Log.e("FIRESTORE_SAVE", e.message.toString())
            }
    }

    private fun addUniqueIdToRegisteredUniqueId(uniqueId: String) {
        val hashMap = java.util.HashMap<String, Any>()
        hashMap["uid"] = "" + firebaseAuth.uid
        hashMap["email"] = "" + email
        hashMap["name"] = "" + fullName
        hashMap["uniqueId"] = "" + uniqueId
        hashMap["phone"] = "" + phoneNumber
        hashMap["userType"] = "" + userType

        val documentReference =
            firebaseFirestore.collection("RegisteredUniqueId").document(uniqueId)
        documentReference
            .set(hashMap)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }
}