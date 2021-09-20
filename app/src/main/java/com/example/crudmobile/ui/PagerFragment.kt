package com.example.crudmobile.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.crudmobile.R
import com.example.crudmobile.adapters.FragmentAdapter
import com.example.crudmobile.controllers.MainActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_pager.*

class PagerFragment : Fragment(R.layout.fragment_pager) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loggedInUser = MainActivity.getLoggedInUser(requireActivity())
        if (loggedInUser != null) {
            "Hello ${loggedInUser.name}!".also { tvLoggedInUser.text = it }
        } else {
            findNavController().navigate(
                PagerFragmentDirections.actionPagerFragmentToLoginFragment()
            )
        }

        btnLogout.setOnClickListener {
            MainActivity.logout(requireActivity())
            findNavController().navigate(
                PagerFragmentDirections.actionPagerFragmentToLoginFragment()
            )
        }

        val adapter = activity?.let { FragmentAdapter(it.supportFragmentManager, lifecycle) }

        pager.adapter = adapter

        TabLayoutMediator(tab_layout, pager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = "CRUD"
                1 -> tab.text = "SMS"
            }
        }.attach()

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {  }

            override fun onTabUnselected(tab: TabLayout.Tab?) {  }

            override fun onTabReselected(tab: TabLayout.Tab?) {  }
        })
    }
}