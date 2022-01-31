package com.ilsamil.conveniencemap.Fragments

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ilsamil.conveniencemap.MainActivity
import com.ilsamil.conveniencemap.MainViewModel
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentDetailBinding
    private lateinit var evalLocation : String

    companion object {
        fun newInstance() : DetailFragment {
            return DetailFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        mainViewModel.detailLiveData.observe( this, Observer {
            binding.detailFaclNmTv.text = it.faclNm
            binding.detailLcMnadTv.text = it.lcMnad
            binding.detailEstbDateTv.text = it.estbDate
            binding.detailTypeTv.text = it.faclTyCd.toString()
            evalLocation = it.faclLat.toString() + "," + it.faclLng.toString()

            if(binding.detailRprnTv.text == "") {
                binding.detailRprnTv.text = "설립자 정보 없음"
            } else {
                binding.detailRprnTv.text = it.faclRprnNm
            }
            mainViewModel.getEvalInfo(it.wfcltId.toString(), "2")
        })


        mainViewModel.evalInfoDetailLiveData.observe(this, Observer {
            for(data in it) {
                val evalLayout = LinearLayout(container?.context)
                val scrapImage = ImageView(container?.context)
                val evalTextView = TextView(container?.context)

                val linearLayout = LinearLayout.LayoutParams(
                    changeDP(75),
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                val imageLayout = LinearLayout.LayoutParams(
                    changeDP(55),
                    changeDP(55)
                )

                val textLayout = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                when (data.evalInfo) {
                    "계단 또는 승강설비" -> {
                        evalTextView.text = "계단"
                        scrapImage.setImageResource(R.drawable.evalinfo_stairs_img)
                    }
                    "대변기" -> {
                        evalTextView.text = "대변기"
                        scrapImage.setImageResource(R.drawable.evalinfo_wc_img)
                    }
                    "복도" -> {
                        evalTextView.text = "복도시설"
                        scrapImage.setImageResource(R.drawable.evalinfo_theaters_img)
                    }
                    "소변기" -> {
                        evalTextView.text = "소변기"
                        scrapImage.setImageResource(R.drawable.evalinfo_wc_img)
                    }
                    "일반사항" -> {
                        evalTextView.text = "일반사항"
                        scrapImage.setImageResource(R.drawable.evalinfo_station_img)
                    }
                    "장애인전용주차구역" -> {
                        evalTextView.text = "장애인주차"
                        scrapImage.setImageResource(R.drawable.evalinfo_parking_img)
                    }
                    "주출입구 높이차이 제거" -> {
                        evalTextView.text = "입구높이"
                        scrapImage.setImageResource(R.drawable.evalinfo_cellular_bar_img)
                    }
                    "주출입구 접근로" -> {
                        evalTextView.text = "접근로"
                        scrapImage.setImageResource(R.drawable.evalinfo_accessible_img)
                    }
                    "출입구(문)" -> {
                        evalTextView.text = "출입구(문)"
                        scrapImage.setImageResource(R.drawable.evalinfo_sensor_door_img)
                    }
                    "해당시설 층수" -> {
                        evalTextView.text = "엘리베이터"
                        scrapImage.setImageResource(R.drawable.evalinfo_elevator_img)
                    }
                    "경보 및 피난설비" -> {
                        evalTextView.text = "피난설비"
                        scrapImage.setImageResource(R.drawable.evalinfo_alert_img)
                    }
                    "매표소" -> {
                        evalTextView.text = "매표소"
                        scrapImage.setImageResource(R.drawable.evalinfo_store_img)
                    }
                    "판매기" -> {
                        evalTextView.text = "판매기"
                        scrapImage.setImageResource(R.drawable.evalinfo_store_front_img)
                    }
                    "음료대" -> {
                        evalTextView.text = "음료대"
                        scrapImage.setImageResource(R.drawable.evalinfo_touch_app_img)
                    }
                    "비치용품" -> {
                        evalTextView.text = "비치용품"
                        scrapImage.setImageResource(R.drawable.evalinfo_group_work_img)
                    }
                    "접수대 및 작업대" -> {
                        evalTextView.text = "작업대"
                        scrapImage.setImageResource(R.drawable.evalinfo_calendar_img)
                    }
                    "관람석 및 열람석" -> {
                        evalTextView.text = "관람석"
                        scrapImage.setImageResource(R.drawable.evalinfo_more_img)
                    }
                    "유도 및 안내 설비" -> {
                        evalTextView.text = "안내설비"
                        scrapImage.setImageResource(R.drawable.evalinfo_more_img)
                    }
                    else -> {
                        evalTextView.text = data.evalInfo
                        scrapImage.setImageResource(R.drawable.evalinfo_more_img)
                    }
                }

                evalTextView.layoutParams = textLayout
                imageLayout.setMargins(0,0,0,changeDP(3))
                scrapImage.layoutParams = imageLayout
                linearLayout.setMargins(changeDP(7), changeDP(7), changeDP(7), changeDP(7))

                evalLayout.orientation = LinearLayout.VERTICAL
                evalLayout.layoutParams = linearLayout
                evalLayout.gravity = Gravity.CENTER
                evalLayout.addView(scrapImage)
                evalLayout.addView(evalTextView)



                binding.detailFlexboxLayout.addView(evalLayout)
            }
        })

        binding.kakaoGetRoadBt.setOnClickListener {
            if (container != null) {
                kakaoGetRoad(container.context)
            }
        }

        binding.kakaoGetRoadViewBt.setOnClickListener {
            kakaoGetRoadView()
        }

        return binding.root
    }

    //dp로 변경
    private fun changeDP(value : Int) : Int{
        var displayMetrics = resources.displayMetrics
        var dp = Math.round(value * displayMetrics.density)
        return dp
    }

    private fun kakaoGetRoad(context : Context) {
        val i = Intent(Intent.ACTION_VIEW)
        Log.d("ttest", "evalLocation = " + evalLocation)

//        val myLocation = mainViewModel.getLocationFacInfo2(context)
//        Log.d("ttest", "myLocation = " + myLocation)

        i.data = Uri.parse("kakaomap://route?ep=$evalLocation&by=CAR")
        startActivity(i)
    }

    private fun kakaoGetRoadView() {
        val i = Intent(Intent.ACTION_VIEW)
        Log.d("ttest", "evalLocation = " + evalLocation)
        i.data = Uri.parse("kakaomap://roadView?p=$evalLocation")
        startActivity(i)
    }


}



















