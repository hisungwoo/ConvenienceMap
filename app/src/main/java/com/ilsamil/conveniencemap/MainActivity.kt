package com.ilsamil.conveniencemap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ilsamil.conveniencemap.Fragments.*
import com.ilsamil.conveniencemap.adapters.EvalinfoAdapter
import com.ilsamil.conveniencemap.databinding.ActivityMainBinding
import com.ilsamil.conveniencemap.model.EvalInfoList
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
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private var backButtonTime = 0L
    private var fragmentCount = 0

    private lateinit var binding: ActivityMainBinding
    private val mapFragment: MapFragment by lazy { MapFragment.newInstance() }
    private val categoryFragment: CategoryFragment by lazy { CategoryFragment.newInstance() }
    private val bookmarkFragment: BookmarkFragment by lazy { BookmarkFragment.newInstance() }
    private val infoFragment: InfoFragment by lazy { InfoFragment.newInstance() }

    private lateinit var searchFragment: SearchFragment
    lateinit var fadeOutAnim : Animation


    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
//            Toast.makeText(this@MainActivity, "위치권한 승인", Toast.LENGTH_SHORT ).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().add(R.id.fragment_view, mapFragment, "map").commit()
        replaceFragment(binding)

        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        binding.resultRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        var instance: Retrofit? = null
        instance = Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
            .build()


        val aapi = instance.create(RetrofitService::class.java)
        val ttest : Call<FacInfoList> = aapi.getEvalInfoList("0000011722")

        ttest.enqueue(object : Callback<FacInfoList> {
            override fun onResponse(call: Call<FacInfoList>, response: Response<FacInfoList>) {
                if(response.isSuccessful()) {
                    val items = response.body()?.servList!!
                    var evalinfo = items[0].evalInfo.toString()
                    Log.d("ttest", evalinfo)

                    val evalinfoList = arrayListOf<EvalInfoList>()
                    var evalinfos = evalinfo.split(",")

                    for (i in evalinfos) {
                        evalinfoList.add(EvalInfoList(i))
                    }

                    val adapter = EvalinfoAdapter()
                    binding.resultRecyclerView.adapter = adapter

                    adapter.updateItems(evalinfoList)


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





//            fadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_down)
//            binding.bottomNav.startAnimation(fadeOutAnim)
//            binding.bottomNav.visibility = View.GONE




//        val items = arrayListOf<ServList>()
//        items.add(ServList("","",1.2,1.2,"","","","","",
//            "","","","계당",""))
//        items.add(ServList("","",1.2,1.2,"","","","","",
//            "","","","계단",""))
//        items.add(ServList("","",1.2,1.2,"","","","","",
//            "","","","높낮이",""))
//        items.add(ServList("","",1.2,1.2,"","","","","",
//            "","","","엘리베이터",""))
//        items.add(ServList("","",1.2,1.2,"","","","","",
//            "","","","높낮이",""))
//        items.add(ServList("","",1.2,1.2,"","","","","",
//            "","","","계단",""))







    }

    private fun replaceFragment(binding: ActivityMainBinding) {
        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.menu_category -> {
                    supportFragmentManager.popBackStackImmediate("category", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_view, categoryFragment, "category").addToBackStack("category").commit()
                }
                R.id.menu_bookmark -> {
                    supportFragmentManager.popBackStackImmediate("bookmark", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_view, bookmarkFragment, "bookmark").addToBackStack("bookmark").commit()
                }
                R.id.menu_map -> {
                    supportFragmentManager.popBackStackImmediate("map", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_view, mapFragment, "map").addToBackStack("map").commit()
                }
                R.id.menu_info -> {
                    supportFragmentManager.popBackStackImmediate("info", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_view, infoFragment, "info").addToBackStack("info").commit()
                }
            }
            true
        }
    }


    // 뒤로가기 시 bottomnav 클릭 활성화
    private fun updateBottomMenu() {
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag("category")
        val tag2: Fragment? = supportFragmentManager.findFragmentByTag("bookmark")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("map")
        val tag4: Fragment? = supportFragmentManager.findFragmentByTag("info")

        binding.bottomNav.apply {
            if(tag1 != null && tag1.isVisible) {
                this.menu.findItem(R.id.menu_category).isChecked = true
            }
            if(tag2 != null && tag2.isVisible) {
                this.menu.findItem(R.id.menu_bookmark).isChecked = true
            }
            if(tag3 != null && tag3.isVisible) {
                this.menu.findItem(R.id.menu_map).isChecked = true
            }
            if(tag4 != null && tag4.isVisible) {
                this.menu.findItem(R.id.menu_info).isChecked = true
            }


//            if (this.visibility == View.VISIBLE) {
//                Log.d("ttest" , "나타남")
//            } else if (this.visibility == View.GONE) {
//                Log.d("ttest", "gone 상태")
//            }


        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        updateBottomMenu()

    }


}