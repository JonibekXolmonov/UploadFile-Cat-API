package com.example.catapiapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.catapiapp.model.Cat
import com.example.catapiapp.networking.ApiClient
import com.example.catapiapp.networking.services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        apiService = ApiClient(requireContext()).createServiceWithAuth(ApiService::class.java)

        apiService.search(20,1,"sleeping cat").enqueue(object :Callback<List<Cat>>{
            override fun onResponse(call: Call<List<Cat>>, response: Response<List<Cat>>) {
                Log.d("TAG", "onResponse: ${response.body()}")
            }

            override fun onFailure(call: Call<List<Cat>>, t: Throwable) {
                Log.d("TAG", "onFailure: $t")
            }

        })
    }

}