package com.example.crudmobile.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.crudmobile.R
import kotlinx.android.synthetic.main.fragment_sms.*

class SmsFragment : Fragment(R.layout.fragment_sms) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createReport.setOnClickListener {
            findNavController().navigate(
                PagerFragmentDirections.actionPagerFragmentToReportFormFragment()
            )
        }

        viewReports.setOnClickListener {
            findNavController().navigate(
                PagerFragmentDirections.actionPagerFragmentToReportsMonitorFragment()
            )
        }
    }
}