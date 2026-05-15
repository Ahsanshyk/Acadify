package com.ahsan.acadify

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.ahsan.acadify.databinding.ActivityProfileEditBinding

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    private var image_uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        checkUser()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.profileIv.setOnClickListener {
            showImagePickDialog()
        }

        binding.updateBtn.setOnClickListener {
            inputData()
        }
    }

    private var name = ""
    private var phoneNumber = ""
    private var email = ""
    private var country = ""
    private var state = ""
    private var city = ""
    private var address = ""
    private var regNo = ""
    private var dob = ""
    private var fatherName = ""
    private var motherName = ""
    private var branch = ""
    private var semester = ""
    private var session = ""
    private var seatType = ""

    private fun inputData() {

        name = binding.nameEt.text.toString().trim()
        phoneNumber = binding.phoneTv.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        country = binding.countryEt.text.toString().trim()
        state = binding.stateEt.text.toString().trim()
        city = binding.cityEt.text.toString().trim()
        address = binding.addressEt.text.toString().trim()
        regNo = binding.regNoEt.text.toString().trim()
        dob = binding.dobEt.text.toString().trim()
        fatherName = binding.fatherNameEt.text.toString().trim()
        motherName = binding.motherNameEt.text.toString().trim()
        branch = binding.branchEt.text.toString().trim()
        semester = binding.semEt.text.toString().trim()
        session = binding.sessionEt.text.toString().trim()
        seatType = binding.seatTypeEt.text.toString().trim()

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show()
            return
        }

        updateProfile()
    }

    private fun updateProfile() {

        binding.progressBar.visibility = View.VISIBLE

        val hashMap = HashMap<String, Any>()

        hashMap["name"] = name
        hashMap["phone"] = phoneNumber
        hashMap["email"] = email
        hashMap["country"] = country
        hashMap["state"] = state
        hashMap["city"] = city
        hashMap["address"] = address
        hashMap["regNo"] = regNo
        hashMap["dob"] = dob
        hashMap["fatherName"] = fatherName
        hashMap["motherName"] = motherName
        hashMap["branch"] = branch
        hashMap["semester"] = semester
        hashMap["session"] = session
        hashMap["seatType"] = seatType

        if (image_uri == null) {

            saveProfileData(hashMap)

        } else {

            val filePath = "profile_images/${firebaseAuth.uid}"

            FirebaseStorage.getInstance()
                .getReference(filePath)
                .putFile(image_uri!!)
                .addOnSuccessListener { task ->

                    task.storage.downloadUrl.addOnSuccessListener { uri ->

                        hashMap["profileImage"] = uri.toString()

                        saveProfileData(hashMap)
                    }
                }
                .addOnFailureListener { e ->

                    binding.progressBar.visibility = View.GONE

                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveProfileData(hashMap: HashMap<String, Any>) {

        firebaseFirestore.collection("Users")
            .document(firebaseAuth.uid!!)
            .set(hashMap, SetOptions.merge())
            .addOnSuccessListener {

                binding.progressBar.visibility = View.GONE

                Toast.makeText(
                    this,
                    "Profile Updated",
                    Toast.LENGTH_SHORT
                ).show()
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

    private fun checkUser() {

        val user = firebaseAuth.currentUser

        if (user == null) {

            startActivity(Intent(this, LoginActivity::class.java))
            finish()

        } else {

            loadMyInfo()

            if (!user.isEmailVerified) {
                binding.emailNotVerifiedRl.visibility = View.VISIBLE
                binding.emailRl.visibility = View.GONE
            } else {
                binding.emailNotVerifiedRl.visibility = View.GONE
                binding.emailRl.visibility = View.VISIBLE
            }
        }
    }

    private fun loadMyInfo() {

        firebaseFirestore.collection("Users")
            .document(firebaseAuth.uid!!)
            .addSnapshotListener(this) { ds, error ->

                if (error != null) return@addSnapshotListener

                if (ds != null && ds.exists()) {

                    binding.nameEt.setText(ds.getString("name") ?: "")
                    binding.phoneTv.setText(ds.getString("phone") ?: "")
                    binding.emailEt.setText(ds.getString("email") ?: "")
                    binding.addressEt.setText(ds.getString("address") ?: "")
                    binding.cityEt.setText(ds.getString("city") ?: "")
                    binding.stateEt.setText(ds.getString("state") ?: "")
                    binding.countryEt.setText(ds.getString("country") ?: "")
                    binding.regNoEt.setText(ds.getString("regNo") ?: "")
                    binding.dobEt.setText(ds.getString("dob") ?: "")
                    binding.fatherNameEt.setText(ds.getString("fatherName") ?: "")
                    binding.motherNameEt.setText(ds.getString("motherName") ?: "")
                    binding.branchEt.setText(ds.getString("branch") ?: "")
                    binding.semEt.setText(ds.getString("semester") ?: "")
                    binding.sessionEt.setText(ds.getString("session") ?: "")
                    binding.seatTypeEt.setText(ds.getString("seatType") ?: "")

                    val profileImage = ds.getString("profileImage") ?: ""

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

    private fun showImagePickDialog() {

        val options = arrayOf("Camera", "Gallery")

        AlertDialog.Builder(this)
            .setTitle("Pick Image")
            .setItems(options) { _, which ->

                if (which == 0) {
                    pickImageCamera()
                } else {
                    pickImageGallery()
                }
            }
            .show()
    }

    private fun pickImageCamera() {

        val values = ContentValues()

        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Profile Image")

        image_uri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)

        cameraActivityResultLauncher.launch(intent)
    }

    private fun pickImageGallery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        galleryActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {
                binding.profileIv.setImageURI(image_uri)
            }
        }

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {

                image_uri = result.data?.data

                binding.profileIv.setImageURI(image_uri)
            }
        }
}