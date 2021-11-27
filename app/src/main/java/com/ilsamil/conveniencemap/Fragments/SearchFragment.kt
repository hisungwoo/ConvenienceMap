package com.ilsamil.conveniencemap.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ilsamil.conveniencemap.MainViewModel
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.databinding.FragmentSearchBinding
import com.ilsamil.conveniencemap.adapters.FacInfoAdapter
import com.ilsamil.conveniencemap.model.ServList

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var imm : InputMethodManager

    val mainViewModel by activityViewModels<MainViewModel>()

    companion object {
        fun newInstance() : SearchFragment {
            return SearchFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel.bottomNavLiveData.value = false
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(container?.context,
            RecyclerView.VERTICAL,
            false
        )

        val adapter = FacInfoAdapter()
        binding.recyclerView.adapter = adapter

        mainViewModel.servLiveData.observe(this, Observer {
            adapter.updateItems(it)
        })

        binding.searchEt.setOnKeyListener { view, i, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.searchEt.windowToken, 0)

                val searchText = binding.searchEt.text.toString()
                mainViewModel.searchFacl(searchText)

//                var instance = Retrofit.Builder()
//                    .baseUrl("http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/")
//                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                    .addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
//                    .build()
//
//                val aapi = instance.create(RetrofitService::class.java)
//                val ttest : Call<FacInfoList> = aapi.getList(15, searchText)
//
//                ttest.enqueue(object : Callback<FacInfoList> {
//                    override fun onResponse(call: Call<FacInfoList>, response: Response<FacInfoList>) {
//                        if(response.isSuccessful()) {
//                            val items = response.body()?.servList!!
//                            adapter.updateItems(items)
//
//                        } else { // code == 400
//                            // 실패 처리
//                            Log.d("tttest" , "dd = 실패")
//                        }
//                    }
//
//                    override fun onFailure(call: Call<FacInfoList>, t: Throwable) {
//                        Log.d("tttest" , "onFailure = " + t)
//                        t.printStackTrace()
//                    }
//
//                })

                true
            } else {
                false
            }
        }

        adapter.setOnItemClickListener(object : FacInfoAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: ServList, pos: Int) {
                val mapFragment: MapFragment by lazy { MapFragment.newInstance() }

                setFragmentResult(
                    "movePin",
                    bundleOf("faclLng" to data.faclLng,
                                    "faclLat" to data.faclLat,
                                    "faclNm" to data.faclNm,
                                    "faclTyCd" to data.faclTyCd,
                                    "lcMnad" to data.lcMnad,
                                    "wfcltId" to data.wfcltId
                    )
                )
                activity?.supportFragmentManager?.popBackStackImmediate("map", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_view, mapFragment, "map")?.addToBackStack("map")?.commit()
            }
        })

        return binding.root
    }


    override fun onStart() {
        super.onStart()
        imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.searchEt.requestFocus()
        imm.showSoftInput(binding.searchEt, InputMethodManager.SHOW_IMPLICIT)
    }

}