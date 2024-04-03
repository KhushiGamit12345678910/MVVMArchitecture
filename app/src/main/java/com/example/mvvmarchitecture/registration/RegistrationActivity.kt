package com.example.mvvmarchitecture.registration

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mvvmarchitecture.R
import com.example.mvvmarchitecture.const.isNetworkAvailable
import com.example.mvvmarchitecture.databinding.ActivityRegistrationBinding
import com.example.mvvmarchitecture.dialogbox.DialogBox
import com.example.mvvmarchitecture.login.LoginActivity
import com.example.mvvmarchitecture.ui.Status
import com.example.viewmodeldemo.utils.PathUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegistrationBinding
    lateinit var registrationViewModel: RegistrationViewModel
    private lateinit var dialog: DialogBox
    private lateinit var processDialog: ProgressDialog
    private lateinit var inputMethodManager: InputMethodManager
    private var isCamera: Boolean = false
    //var imagePath : String=""

    companion object {
        private const val IMAGE_CHOOSE = 100
        private const val CAMERA_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        registrationViewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]

        inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        // Initialize
        binding.registrationViewModel = registrationViewModel
        dialog = DialogBox(this)
        processDialog = ProgressDialog(this)

        registrationViewModel.validationLiveData.observe(this) {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }

        registrationViewModel.dataLiveData.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    processDialog.dismiss()
                    Log.d("data_information", "Status.SUCCESS ${it.data?.message}")
                    if (it.data?.status == true) {
                        dialog.commonDialog(
                            resources.getString(R.string.app_name),
                            "register",
                            it.data.message.toString(),
                            1
                        )
                    } else {
                        dialog.commonDialog(
                            resources.getString(R.string.app_name),
                            "register",
                            it.data?.message.toString(),
                            0
                        )
                    }
                }
                Status.LOADING -> {
                    processDialog.setMessage(resources.getString(R.string.loading))
                    processDialog.setCancelable(false)
                    processDialog.show()

                    Log.d("data_information", "Status.LOADING")
                }
                Status.ERROR -> {
                    processDialog.dismiss()
                    Log.d("data_information", "Status.ERROR")
                }
            }
        }

        binding.loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))

        }

        binding.signupButton.setOnClickListener {

            registrationViewModel.nameerror = ""
            registrationViewModel.emailerror = ""
            registrationViewModel.passworderror = ""

            if (registrationViewModel.rgValidation(registrationViewModel.imagePath)) {

                if (isNetworkAvailable(this@RegistrationActivity)) {
                    registrationViewModel.registerUser(registrationViewModel.imagePath)

                } else {
                    Toast.makeText(
                        this@RegistrationActivity,
                        resources.getString(R.string.internet_connection_not_available),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                binding.signupFullname.helperText = registrationViewModel.nameerror
                binding.signupEmail.helperText = registrationViewModel.emailerror
                binding.signupPassword.helperText = registrationViewModel.passworderror
            }
        }

        binding.selectedImage.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    /* private fun checkGalleryPermission() {
         if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
             val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
             requestPermissions(permissions, IMAGE_CHOOSE)
         } else {
             //openGallery()
         }
     }*/

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                picMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                val showRationale =
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                goToSetting(showRationale)
            }
        }

    private val picMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.selectedImage.setImageURI(uri)
                registrationViewModel.imagePath = PathUtil.getPath(this, uri).toString()
                Glide.with(binding.selectedImage).load(uri).placeholder(R.drawable.placeholder)
                    .into(binding.selectedImage)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    fun openCamera() {
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(camera, CAMERA_REQUEST_CODE)
    }

    // setting function for go to setting for permission
    private fun goToSetting(showRationale: Boolean) {
        AlertDialog.Builder(this)
            .setMessage("It look like you have turned off permission" + " It can be enable under App settings !!!")
            .setPositiveButton("GO TO SETTING") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }


    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CHOOSE) {
            if (data!= null){
                val uri = data.data
                binding.selectedImage.setImageURI(uri)
                viewBinding.imagePath = PathUtil.getPath(this,uri)!!.toString()
                Glide.with(binding.selectedImage).load(uri).placeholder(R.drawable.placeholder).into(binding.selectedImage)
            }else{
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "no match request code", Toast.LENGTH_SHORT).show()
        }
    }*/

    /* override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         if (requestCode == IMAGE_CHOOSE) {
             if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 Toast.makeText(this@RegistrationActivity, "Gallery Permission Granted", Toast.LENGTH_SHORT).show()
                 //openGallery()
             } else {
                 Toast.makeText(this@RegistrationActivity, "Gallery Permission Denied", Toast.LENGTH_SHORT).show()
             }
         }
     }*/

}