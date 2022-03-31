package com.ilsamil.conveniencemap.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.ilsamil.conveniencemap.BuildConfig
import com.ilsamil.conveniencemap.MainActivity
import com.ilsamil.conveniencemap.MainViewModel
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.databinding.FragmentDetailBinding
import com.ilsamil.conveniencemap.utils.Util

/*
    상세보기 프래그먼트

    Google Street View API 를 통해 시설 이미지를 return Glide를 통해 이미지를 표시
    LiveData를 통해 편의시설 정보를 가져오고 표시
    카카오맵 길찾기, 로드 뷰 연동

 */

class DetailFragment : Fragment() {
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentDetailBinding
    private lateinit var evalLocation : String
    private lateinit var callback: OnBackPressedCallback
    private lateinit var util : Util

    companion object {
        fun newInstance() : DetailFragment {
            return DetailFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mainViewModel.isClickImg) {
                    clickBackImg()
                } else {
                    activity?.supportFragmentManager?.popBackStack()
                    if(mainViewModel.isSelectMarker) mainViewModel.mainStatus.value = 7
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
        util = Util()

        mainViewModel.apply {
            // 편의시설 이미지 표시
            detailLiveData.observe( this@DetailFragment, Observer {
                binding.detailFaclNmTv.text = it.faclNm
                binding.detailLcMnadTv.text = it.lcMnad
                binding.detailProgressBar.visibility = View.VISIBLE

                val lat = it.faclLat
                val lng = it.faclLng

                // Goole Street View API
                val WEB_VIEW_URL = "https://maps.googleapis.com/maps/api/streetview?size=420x300&return_error_code=true&location=$lat,$lng&key=${BuildConfig.GOOGLE_API_KEY}"

                // Glide를 사용하여 return 된 이미지 로딩
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

                binding.detailTypeTv.text = util.changeFaclType(it.faclTyCd.toString())
                evalLocation = it.faclLat.toString() + "," + it.faclLng.toString()

                if(it.faclRprnNm == "" || it.faclRprnNm == null) {
                    binding.detailRprnTv.text = "설립자 정보 없음"
                } else {
                    binding.detailRprnTv.text = it.faclRprnNm
                }
                mainViewModel.getEvalInfo(it.wfcltId.toString(), "2")
            })


            // 편의시설 정보 표시
            evalInfoDetailLiveData.observe(this@DetailFragment, Observer {
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

                    evalTextView.text = util.changeEvalText(data.evalInfo)
                    util.changeEvalImg(data.evalInfo).let {
                            it1 -> scrapImage.setImageResource(it1)
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
        }

        binding.apply {

            detailImg.setOnClickListener {
                if (!mainViewModel.isClickImg) {
                    mainViewModel.isClickImg = true
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

            detailBackImg.setOnClickListener {
                val detailFragment = activity?.supportFragmentManager?.findFragmentByTag("detail")
                if (detailFragment != null) {
                    mainViewModel.mainStatus.value = 1
                    activity?.supportFragmentManager?.beginTransaction()?.remove(detailFragment)?.commit()
                }
            }

            kakaoGetRoadBt.setOnClickListener {
                if (container != null) {
                    kakaoGetRoad(container.context)
                }
            }

            kakaoGetRoadViewBt.setOnClickListener {
                kakaoGetRoadView()
            }

            clickCloseBt.setOnClickListener {
                clickBackImg()
            }
        }

        return binding.root
    }

    // 사이즈 dp로 변경
    private fun changeDP(value : Int) : Int{
        val displayMetrics = resources.displayMetrics
        val dp = Math.round(value * displayMetrics.density)
        return dp
    }

    // 이미지 확대 시 닫기 버튼 클릭
    private fun clickBackImg() {
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
        mainViewModel.isClickImg = false
    }

    // 카카오 길찾기 버튼 클릭
    private fun kakaoGetRoad(context : Context) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse("kakaomap://route?ep=$evalLocation&by=CAR")
        startActivity(i)
    }

    // 카카오 로드뷰 보기 클릭
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



















