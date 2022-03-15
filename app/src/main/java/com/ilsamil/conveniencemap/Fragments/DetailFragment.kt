package com.ilsamil.conveniencemap.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.ilsamil.conveniencemap.MainViewModel
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.databinding.FragmentDetailBinding
import com.ilsamil.conveniencemap.utils.Util

class DetailFragment : Fragment() {
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentDetailBinding
    private lateinit var evalLocation : String
    private lateinit var callback: OnBackPressedCallback


    companion object {
        fun newInstance() : DetailFragment {
            return DetailFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mainViewModel.clickImgStatus) {
                    clickBackImg()
                } else {
                    activity?.supportFragmentManager?.popBackStack()
                    if(mainViewModel.selectMarkerStatus) mainViewModel.mainStatus.value = 7
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)

        binding.detailScrollview.post {
            binding.detailScrollview.fullScroll(ScrollView.FOCUS_UP)
        }

        mainViewModel.detailLiveData.observe( this, Observer {
            binding.detailFaclNmTv.text = it.faclNm
            binding.detailLcMnadTv.text = it.lcMnad
            binding.detailProgressBar.visibility = View.VISIBLE

            val lat = it.faclLat
            val lng = it.faclLng
            val API_KEY = "AIzaSyBflVZNYF1HZGJFC8WPd5v0GkqT6nVjDyM"
            val WEB_VIEW_URL = "https://maps.googleapis.com/maps/api/streetview?size=420x300&return_error_code=true&location=$lat,$lng&key=$API_KEY"

            if (container != null) {
                Glide.with(container.context)
                    .load(WEB_VIEW_URL)
                    .error(R.drawable.image_error)
                    .into(binding.detailImg)
            }


            if(it.estbDate != null) {
                val estbDate = it.estbDate.substring(0,4) + "년 " +
                        it.estbDate.substring(4,6) + "월 " +
                        it.estbDate.substring(6) + "일 설립"

                binding.detailEstbDateTv.text = estbDate
            } else {
                binding.detailEstbDateTv.text = "설립날짜 정보 없음"
            }

            val changeType = Util()
            binding.detailTypeTv.text = changeType.changeType(it.faclTyCd.toString())
            evalLocation = it.faclLat.toString() + "," + it.faclLng.toString()

            if(it.faclRprnNm == "" || it.faclRprnNm == null) {
                binding.detailRprnTv.text = "설립자 정보 없음"
            } else {
                binding.detailRprnTv.text = it.faclRprnNm
            }
            mainViewModel.getEvalInfo(it.wfcltId.toString(), "2")
        })


        mainViewModel.evalInfoDetailLiveData.observe(this, Observer {
            binding.detailFlexboxLayout.removeAllViews()
            for(data in it) {
                val evalLayout = LinearLayout(container?.context)
                val scrapImage = ImageView(container?.context)
                val evalTextView = TextView(container?.context)

                val linearLayout = LinearLayout.LayoutParams (
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
                    "샤워실 및 탈의실" -> {
                        evalTextView.text = "샤워·탈의실"
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
                binding.detailProgressBar.visibility = View.GONE
            }
        })

        binding.detailImg.setOnClickListener {
            if (!mainViewModel.clickImgStatus) {
                mainViewModel.clickImgStatus = true
                val constIn = binding.detailConstraintIn as ViewGroup
                constIn.removeView(binding.detailImg)

                binding.clickImgView.visibility = View.VISIBLE
                binding.clickCloseBt.visibility = View.VISIBLE

                val deImage = binding.detailImg as PhotoView
                val clickImgLayout = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                deImage.layoutParams = clickImgLayout
                binding.clickCloseBt.bringToFront()
                binding.detailConsLayout.addView(deImage)
            }
        }

        binding.detailBackImg.setOnClickListener {
            val detailFragment = activity?.supportFragmentManager?.findFragmentByTag("detail")
            if (detailFragment != null) {
                mainViewModel.mainStatus.value = 1
                activity?.supportFragmentManager?.beginTransaction()?.remove(detailFragment)?.commit()
            }
        }

        binding.kakaoGetRoadBt.setOnClickListener {
            if (container != null) {
                kakaoGetRoad(container.context)
            }
        }

        binding.kakaoGetRoadViewBt.setOnClickListener {
            kakaoGetRoadView()
        }

        //이미지 확대 후 X버튼 클릭 시
        binding.clickCloseBt.setOnClickListener {
            clickBackImg()
        }


        return binding.root
    }

    //dp로 변경
    private fun changeDP(value : Int) : Int{
        val displayMetrics = resources.displayMetrics
        val dp = Math.round(value * displayMetrics.density)
        return dp
    }

    private fun clickBackImg() {
        Log.d("ttest", "clickBackImg 실행")
        val constLayout = binding.detailConsLayout as ViewGroup
        constLayout.removeView(binding.detailImg)

        binding.clickImgView.visibility = View.GONE
        binding.clickCloseBt.visibility = View.GONE

        val detailImg = binding.detailImg as PhotoView
        val clickImgLayout = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            changeDP(300)
        )
        detailImg.layoutParams = clickImgLayout
        binding.detailConstraintIn.addView(detailImg)
        mainViewModel.clickImgStatus = false
    }

    private fun kakaoGetRoad(context : Context) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse("kakaomap://route?ep=$evalLocation&by=CAR")
        startActivity(i)
    }

    private fun kakaoGetRoadView() {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse("kakaomap://roadView?p=$evalLocation")
        startActivity(i)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}



















