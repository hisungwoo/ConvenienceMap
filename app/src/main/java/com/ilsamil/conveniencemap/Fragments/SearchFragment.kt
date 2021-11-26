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
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.databinding.FragmentSearchBinding
import com.ilsamil.conveniencemap.adapters.FacInfoAdapter
import com.ilsamil.conveniencemap.model.FacInfoList
import com.ilsamil.conveniencemap.model.ServList
import com.ilsamil.conveniencemap.repository.RetrofitService
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var imm : InputMethodManager
    private lateinit var bottomNav : BottomNavigationView
    lateinit var fadeOutAnim : Animation

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
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(container?.context,
            RecyclerView.VERTICAL,
            false
        )

        fadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.bottom_down)
        bottomNav = activity?.findViewById(R.id.bottom_nav)!!
        bottomNav.startAnimation(fadeOutAnim)
        bottomNav.visibility = View.GONE



        val adapter = FacInfoAdapter()
        binding.recyclerView.adapter = adapter

        binding.searchEt.setOnKeyListener { view, i, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.searchEt.windowToken, 0)

                val searchText = binding.searchEt.text.toString()
                var instance: Retrofit? = null

                instance = Retrofit.Builder()
                    .baseUrl("http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
                    .build()

                val aapi = instance.create(RetrofitService::class.java)
                val ttest : Call<FacInfoList> = aapi.getList(15, searchText)

                ttest.enqueue(object : Callback<FacInfoList> {
                    override fun onResponse(call: Call<FacInfoList>, response: Response<FacInfoList>) {
                        if(response.isSuccessful()) {
                            val items = response.body()?.servList!!
                            adapter.updateItems(items)

                        } else { // code == 400
                            // 실패 처리
                            Log.d("tttest" , "dd = 실패")
                        }
                    }

                    override fun onFailure(call: Call<FacInfoList>, t: Throwable) {
                        Log.d("tttest" , "onFailure = " + t)
                        t.printStackTrace()
                    }

                })

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