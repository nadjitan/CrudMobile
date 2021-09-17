package com.example.crudmobile.controllers

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.crudmobile.R
import com.example.crudmobile.db.UserDatabase.Companion.KEY_LOGGED_IN

class MainActivity : AppCompatActivity() {

    companion object {
        /**
         * Remove stored user in [SharedPreferences]
         * @param activity [Activity]
         * @return void
         */
        fun logout(activity: Activity?) {
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
            with (sharedPref.edit()) {
                remove(KEY_LOGGED_IN)
                apply()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction().apply {
//                replace(R.id.flFragment, LoginFragment())
//                // addToBackStack(null)
//                commit()
//            }
//        }
    }
}