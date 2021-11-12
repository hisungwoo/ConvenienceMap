package com.ilsamil.conveniencemap.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.databinding.FragmentMapBinding
import com.ilsamil.conveniencemap.databinding.FragmentSearchBinding
import com.ilsamil.conveniencemap.model.FacInfoAdapter
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



        val adapter = FacInfoAdapter()
        binding.recyclerView.adapter = adapter

//        val items = listOf(
//            ServList("111", "111", 1.2, 1.2, "인크커피", "대표자", "카페", "서울 금천구 가산디지털2로 127-20 (가산동)"
//                , "Y", "영업", "구분", "ID"
//            ),
//            ServList("111", "111", 1.2, 1.2, "투썸플레이스", "대표자", "카페", "서울 가산동 20-1 오류 243-1"
//                , "Y", "영업", "구분", "ID"
//            ),
//            ServList("111", "111", 1.2, 1.2, "스타벅스 구로점", "대표자", "카페", "서울 구로구 35-230 오리오피스텔"
//                , "Y", "영업", "구분", "ID"
//            ),
//            ServList("111", "111", 1.2, 1.2, "인크커피", "대표자", "카페", "서울 금천구 가산디지털2로 127-20 (가산동)"
//                , "Y", "영업", "구분", "ID"
//            )
//        )

//        adapter.updateItems(items)


        binding.searchEt.setOnKeyListener { view, i, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.searchEt.windowToken, 0)

                val searchText = binding.searchEt.text.toString()
                Log.d("ttest", "검색 텍스트 = " + searchText)

                var instance: Retrofit? = null
                instance = Retrofit.Builder()
                    .baseUrl("http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
                    .build()

                val aapi = instance.create(RetrofitService::class.java)
                val ttest : Call<FacInfoList> = aapi.getList(2, searchText)

                ttest.enqueue(object : Callback<FacInfoList> {
                    override fun onResponse(call: Call<FacInfoList>, response: Response<FacInfoList>) {
                        if(response.isSuccessful()) {
                            val items =
//                            for (i in response.body()?.servList!!) {
//
//                            }


                            Log.d("tttest" , "dd = " + response.body()!!.totalCount)
                            Log.d("tttest" , "dd = " + response.body()!!.servList[0].faclNm)
                            Log.d("tttest" , "dd = " + response.body()!!.servList[1].faclNm)

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






        return binding.root
    }


    override fun onStart() {
        super.onStart()
        imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.searchEt.requestFocus()
        imm.showSoftInput(binding.searchEt, InputMethodManager.SHOW_IMPLICIT)
    }

}