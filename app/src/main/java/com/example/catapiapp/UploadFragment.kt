package com.example.catapiapp

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.catapiapp.model.Cat
import com.example.catapiapp.networking.ApiClient
import com.example.catapiapp.networking.services.ApiService
import com.google.android.material.button.MaterialButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream


class UploadFragment : Fragment(R.layout.fragment_upload) {

    lateinit var ivPhoto: ImageView
    lateinit var btnUpload: MaterialButton

    private lateinit var apiService: ApiService

    lateinit var fileUri: Uri
    lateinit var file: File

    val PICK_IMAGE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        apiService = ApiClient(requireContext()).createServiceWithAuth(ApiService::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
    }

    private fun initViews(view: View) {
        ivPhoto = view.findViewById(R.id.ivPhoto)
        btnUpload = view.findViewById(R.id.btnUpload)

        ivPhoto.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    listOf(Manifest.permission.READ_EXTERNAL_STORAGE).toTypedArray(),
                    2000
                )
            } else {
                startGallery()
            }
        }

        btnUpload.setOnClickListener {

            val reqFile: RequestBody = RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("file", file.name, reqFile)

            apiService.uploadFile(body, "sub_idjjhdbid").enqueue(object : Callback<Cat> {
                override fun onResponse(call: Call<Cat>, response: Response<Cat>) {
                    Log.d("TAG", "onResponse: ${response.body()}")
                }

                override fun onFailure(call: Call<Cat>, t: Throwable) {
                    Log.d("TAG", "onFailure: $t")
                }

            })
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            fileUri = data?.data!!

            val ins = requireActivity().contentResolver.openInputStream(fileUri)
            file = File.createTempFile(
                "file",
                ".jpg",
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )

            val fileOutputStream = FileOutputStream(file)

            ins?.copyTo(fileOutputStream)
            ins?.close()
            fileOutputStream.close()

            if (file.length() != 0L) {
                Glide.with(requireContext())
                    .load(file)
                    .into(ivPhoto)
            }

            btnUpload.visibility = View.VISIBLE
        }
    }
}