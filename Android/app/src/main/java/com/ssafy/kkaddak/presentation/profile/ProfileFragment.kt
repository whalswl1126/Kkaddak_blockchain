package com.ssafy.kkaddak.presentation.profile

import com.google.android.material.tabs.TabLayoutMediator
import com.ssafy.kkaddak.R
import com.ssafy.kkaddak.databinding.FragmentProfileBinding
import com.ssafy.kkaddak.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    override fun initView() {
        initTabLayout()
        initListener()
    }

    private fun initListener() {
        binding.ivUploadMusic.setOnClickListener {
            navigate(ProfileFragmentDirections.actionProfileFragmentToUploadMusicFragment())
        }
    }

    private fun initTabLayout() {
        val tabIcons = listOf(R.drawable.ic_profile_tab_song, R.drawable.ic_profile_tab_nft)
        binding.apply {
            vpCuration.adapter = ProfileAdapter(this@ProfileFragment)
            TabLayoutMediator(
                tlProfile, vpCuration
            ) { tab, position ->
                tab.setIcon(tabIcons[position])
            }.attach()
        }
    }
}