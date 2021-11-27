package com.ilsamil.conveniencemap.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ilsamil.conveniencemap.MainViewModel
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.adapters.EvalinfoAdapter
import com.ilsamil.conveniencemap.databinding.ActivityMainBinding
import com.ilsamil.conveniencemap.databinding.FragmentMapBinding
import com.ilsamil.conveniencemap.model.EvalInfoList
import com.ilsamil.conveniencemap.model.FacInfoList
import com.ilsamil.conveniencemap.repository.RetrofitService
import com.ilsamil.conveniencemap.utils.ChangeType
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class MapFragment : Fragment() {
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentMapBinding
    private lateinit var searchFragment : SearchFragment
    private lateinit var mapView: MapView
    lateinit var fadeInAnim : Animation



    companion object {
        fun newInstance() : MapFragment {
            return MapFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = MapView(activity)
        mainViewModel.bottomNavLiveData.value = true

        setFragmentResultListener("movePin") { requestKey, bundle ->
            mainViewModel.bottomNavLiveData.value = false
            val faclLng = bundle.getDouble("faclLng")
            val faclLat = bundle.getDouble("faclLat")
            val faclNm = bundle.getString("faclNm")
            val faclTyCd = bundle.getString("faclTyCd")
            val lcMnad = bundle.getString("lcMnad")
            val wfcltId = bundle.getString("wfcltId")!!

            fadeInAnim = AnimationUtils.loadAnimation(container?.context, R.anim.bottom_up)

            Log.d("ttest" , "faclLng " + faclLng)
            Log.d("ttest" , "faclLat " + faclLat)
            Log.d("ttest" , "faclNm " + faclNm)

            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(faclLng, faclLat), 0, true);

            val customMarker = MapPOIItem()
            customMarker.itemName = "테스트 마커"
            customMarker.tag = 1
            customMarker.mapPoint = MapPoint.mapPointWithGeoCoord(faclLng, faclLat)
            customMarker.markerType = MapPOIItem.MarkerType.CustomImage
            customMarker.customImageResourceId = R.drawable.ic_location_pin_50_2
            customMarker.isCustomImageAutoscale = false
            customMarker.setCustomImageAnchor(0.5f, 1.0f)

            mapView.addPOIItem(customMarker)

            binding.resultRecyclerView.layoutManager = LinearLayoutManager(container?.context, RecyclerView.HORIZONTAL, false)
            var instance: Retrofit? = null
            instance = Retrofit.Builder()
                .baseUrl("http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
                .build()


            val aapi = instance.create(RetrofitService::class.java)
            val ttest : Call<FacInfoList> = aapi.getEvalInfoList(wfcltId)

            ttest.enqueue(object : Callback<FacInfoList> {
                override fun onResponse(call: Call<FacInfoList>, response: Response<FacInfoList>) {
                    if(response.isSuccessful()) {
                        val items = response.body()?.servList!!
                        var evalinfo = items[0].evalInfo.toString()
                        Log.d("ttest", evalinfo)

                        val evalinfoList = arrayListOf<EvalInfoList>()
                        var evalinfos = evalinfo.split(",")

                        for (i in evalinfos) {
                            Log.d("ttest", "i = " + i.trim())
                            evalinfoList.add(EvalInfoList(i.trim()))
                        }

                        val adapter = EvalinfoAdapter()
                        binding.resultRecyclerView.adapter = adapter
                        val faclTycdChange = faclTyCd?.let { ChangeType().changeType(it) }


                        binding.resultNmTv.text = faclNm
                        binding.resultTypeTv.text = faclTycdChange
                        binding.resultLocationTv.text = lcMnad


                        adapter.updateItems(evalinfoList)
                        binding.resultLayout.startAnimation(fadeInAnim)
                        binding.resultLayout.visibility = View.VISIBLE


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

        }


        binding.clKakaoMapView.addView(mapView)

        binding.searchBtn.setOnClickListener{
            searchFragment = SearchFragment.newInstance()
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_view, searchFragment)?.addToBackStack(null)?.commit()
        }


        return binding.root
    }
}