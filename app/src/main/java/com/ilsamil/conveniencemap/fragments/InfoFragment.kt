package com.ilsamil.conveniencemap.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.ilsamil.conveniencemap.MainViewModel
import com.ilsamil.conveniencemap.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentInfoBinding

    companion object {
        fun newInstance() : InfoFragment {
            return InfoFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel.mainStatus.value = 8
        binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

}