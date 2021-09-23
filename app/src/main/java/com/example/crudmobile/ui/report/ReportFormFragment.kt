package com.example.crudmobile.ui.report

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.crudmobile.R
import kotlinx.android.synthetic.main.fragment_report_form.*
import android.app.TimePickerDialog
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.crudmobile.db.IncidentReportDatabase
import com.example.crudmobile.models.IncidentReport
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class ReportFormFragment : Fragment(R.layout.fragment_report_form) {

    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0

    private var incidentType = ""

    private val permissionRequest = 101

    companion object {
        /**
         * Used for fixing missing zeros in TimePickerDialog
         * @param number [Int]
         * @return [Int]
         */
        fun checkDigit(number: Int) = if (number <= 9) "0$number" else number.toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        btn_date.setOnClickListener {
            // Get Current Date
            val c: Calendar = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), R.style.DateTimePickerTheme,
                { _, year, monthOfYear, dayOfMonth ->
                    "${year}-${monthOfYear + 1}-${dayOfMonth}".also { in_date.text = it }
                }, mYear, mMonth, mDay
            ).show()
        }

        btn_time.setOnClickListener {
            // Get Current Time
            val c: Calendar = Calendar.getInstance()
            mHour = c.get(Calendar.HOUR_OF_DAY)
            mMinute = c.get(Calendar.MINUTE)

            // Launch Time Picker Dialog
            TimePickerDialog(requireContext(), R.style.DateTimePickerTheme,
                { _, hourOfDay, minute ->
                    "${checkDigit(hourOfDay)}:${checkDigit(minute)}".also { in_time.text = it }
                },
                mHour,
                mMinute,
                false
            ).show()
        }

        // Set MaterialButton styles
        btnInjury.setOnClickListener {
            incidentType = "Injury"
            btnInjury.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.navy_blue)
            btnInjury.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.green_light))
            btnInjury.setIconTintResource(R.color.green_light)
            btnNearMiss.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_light)
            btnNearMiss.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.navy_blue))
            btnNearMiss.setIconTintResource(R.color.navy_blue)
            btnDeath.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_light)
            btnDeath.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.navy_blue))
            btnDeath.setIconTintResource(R.color.navy_blue)
        }
        btnNearMiss.setOnClickListener {
            incidentType = "Near Miss"
            btnInjury.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_light)
            btnInjury.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.navy_blue))
            btnInjury.setIconTintResource(R.color.navy_blue)
            btnNearMiss.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.navy_blue)
            btnNearMiss.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.green_light))
            btnNearMiss.setIconTintResource(R.color.green_light)
            btnDeath.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_light)
            btnDeath.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.navy_blue))
            btnDeath.setIconTintResource(R.color.navy_blue)
        }
        btnDeath.setOnClickListener {
            incidentType = "Death"
            btnInjury.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_light)
            btnInjury.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.navy_blue))
            btnInjury.setIconTintResource(R.color.navy_blue)
            btnNearMiss.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_light)
            btnNearMiss.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.navy_blue))
            btnNearMiss.setIconTintResource(R.color.navy_blue)
            btnDeath.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.navy_blue)
            btnDeath.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.green_light))
            btnDeath.setIconTintResource(R.color.green_light)
        }

        btnSubmitForm.setOnClickListener {
            saveIncidentReport()
        }
    }

    /**
     * Save an [IncidentReport] to database then show snackbar and send SMS message.
     * @return void
     */
    private fun saveIncidentReport() {
        val reporter = etReporter.text.toString().trim()
        val dateTime = "$mYear-${mMonth + 1}-${mDay} $mHour:$mMinute"
        val incidentDescription = etIncidentDescription.text.toString().trim()
        val witnesses = etWitnesses.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val phoneNumber = etPhoneNumber.text.toString()

        val databaseHandler = IncidentReportDatabase(requireContext())

        if (reporter.isNotEmpty() && dateTime.isNotEmpty() &&
            dateTime.isNotEmpty() && incidentType.isNotEmpty() &&
            incidentDescription.isNotEmpty() && witnesses.isNotEmpty() &&
            location.isNotEmpty()) {

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

            val status = databaseHandler.addIncidentReport(
                IncidentReport(0,
                    reporter = reporter,
                    dateTime = sdf.parse(dateTime),
                    incidentType = incidentType,
                    incidentDescription = incidentDescription,
                    witnesses = witnesses.split(",").map { it.trim() },
                    phoneNumber = phoneNumber.toLong(),
                    location = location
                )
            )

            if (status > -1) {
                sendMessage()
            }
        } else {
            Snackbar.make(
                clIncidentReportForm,
                "All fields must not be blank.",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun sendMessage() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.SEND_SMS
        )

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            myMessage()
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.SEND_SMS), permissionRequest)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults:IntArray
    ) {
        if (requestCode== permissionRequest) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                myMessage()
            } else {
                Snackbar.make(
                    clIncidentReportForm,
                    "You don't have required permission to send a message.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun myMessage() {
        val reporter = etReporter.text.toString().trim()
        val dateTime = "$mYear-${mMonth + 1}-${mDay} $mHour:$mMinute"
        val incidentType = this.incidentType
        val incidentDescription = etIncidentDescription.text.toString().trim()
        val witnesses = etWitnesses.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val phoneNumber = etPhoneNumber.text.toString().trim()

        val currDate = SimpleDateFormat(
            "EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.getDefault())
            .parse(
                SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.getDefault())
                    .format(Calendar.getInstance().time)
            )

        val myNumber = etPhoneNumber.text.toString()
        val myMsg = "INCIDENT REPORT " +
                    "\nReporter: $reporter" +
                    "\nDate & Time: $dateTime" +
                    "\nIncident Type: $incidentType" +
                    "\nIncident Description: $incidentDescription" +
                    "\nWitnesses: $witnesses" +
                    "\nLocation: $location" +
                    "\nPhone Number: $phoneNumber" +
                    "\nReport Creation Date: $currDate"

        if (myNumber.isNotEmpty()) {
            if (TextUtils.isDigitsOnly(myNumber)) {
                val smsManager: SmsManager = SmsManager.getDefault()

                try {
                    val parts = smsManager.divideMessage(myMsg)

                    smsManager.sendMultipartTextMessage(
                        phoneNumber, null, parts,
                        null, null
                    )

                    Snackbar.make(
                        clIncidentReportForm,
                        "Incident report saved and message sent to $phoneNumber",
                        Snackbar.LENGTH_LONG
                    ).show()

                    etReporter.text.clear()
                    in_date.text = ""
                    in_time.text = ""
                    this.incidentType = ""
                    etIncidentDescription.text.clear()
                    etWitnesses.text.clear()
                    etLocation.text.clear()
                    etPhoneNumber.text.clear()
                } catch (ex: java.lang.Exception) {
                    Snackbar.make(
                        clIncidentReportForm,
                        ex.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()

                    ex.printStackTrace()
                }
            } else {
                Snackbar.make(
                    clIncidentReportForm,
                    "Please enter the correct number.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        } else {
            Snackbar.make(
                clIncidentReportForm,
                "Field cannot be empty.",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}