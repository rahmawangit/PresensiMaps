package com.example.presensiusingmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.presensiusingmap.data.Presensi
import com.example.presensiusingmap.data.PresensiViewModel


class LihatPresensi : AppCompatActivity() {
//    private lateinit var binding : ActivityLihatPresensiBinding
    private lateinit var  mPresensiViewModel: PresensiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lihat_presensi)

//        binding = ActivityLihatPresensiBinding.inflate(layoutInflater)

        val adapter = PresensiListAdapter()
//        val recyclerView = binding.tvRecyle
        val recyclerView = findViewById<RecyclerView>(R.id.tv_recyle)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        mPresensiViewModel = ViewModelProvider(this).get(PresensiViewModel::class.java)
//        mPresensiViewModel.readAllData.observe(viewLifecycleOwner, Observer {
//                user -> adapter.setData(user)
//        })


        val observer = Observer<List<Presensi>> { presensi ->
            // Update the UI or perform actions based on the LiveData changes
            adapter.setData(presensi)
        }
        mPresensiViewModel.readAllData.observeForever(observer)
//        setContentView(binding.root)
//        mPresensiViewModel.readAllData.removeObserver(observer)


    }
}