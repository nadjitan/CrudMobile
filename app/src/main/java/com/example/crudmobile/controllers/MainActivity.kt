package com.example.crudmobile.controllers

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.crudmobile.R
import com.example.crudmobile.db.UserDatabase.Companion.KEY_LOGGED_IN
import android.widget.EditText
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.crudmobile.models.User
import com.google.android.material.textfield.TextInputLayout
import kotlin.math.abs
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private var downX: Int = 0

    companion object {
        /**
         * Remove stored user in [SharedPreferences]
         * @param activity Type of [Activity]
         * @return void
         */
        fun logout(activity: Activity) {
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
            with (sharedPref.edit()) {
                remove(KEY_LOGGED_IN)
                apply()
            }
        }

        /**
         * Set logged in user in [SharedPreferences]
         * @param activity Type of [Activity]
         * @return void
         */
        fun setLoggedInUser(activity: Activity, user: User) {
            val gson = Gson()
            val json = gson.toJson(user)

            // Store user locally
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
            with (sharedPref.edit()) {
                putString(KEY_LOGGED_IN, json)
                apply()
            }
        }

        /**
         * Get logged in user in [SharedPreferences]
         * @param activity Type of [Activity]
         * @return [User]
         */
        fun getLoggedInUser(activity: Activity): User {
            val gson = Gson()

            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
            val json = sharedPref.getString(KEY_LOGGED_IN, "User not found")

            return gson.fromJson(json, User::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
    }

    /**
     * Made to let user touch outside an [EditText] or [TextInputLayout] and close keyboard.
     * @see [stackoverflow](https://stackoverflow.com/a/61290481)
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            downX = event.rawX.toInt()
        }
        if (event.action == MotionEvent.ACTION_UP) {
            val v: View? = currentFocus
            if (v is EditText || v is TextInputLayout) {
                val x = event.rawX.toInt()
                val y = event.rawY.toInt()
                //Was it a scroll - If skip all
                if (abs(downX - x) > 5) {
                    return super.dispatchTouchEvent(event)
                }
                val reducePx = 25
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                //Bounding box is to big, reduce it just a little bit
                outRect.inset(reducePx, reducePx)
                if (!outRect.contains(x, y)) {
                    v.clearFocus()
                    var touchTargetIsEditText = false
                    //Check if another editText has been touched
                    for (vi in v.rootView.touchables) {
                        if (vi is EditText || vi is TextInputLayout) {
                            val clickedViewRect = Rect()
                            vi.getGlobalVisibleRect(clickedViewRect)
                            //Bounding box is to big, reduce it just a little bit
                            clickedViewRect.inset(reducePx, reducePx)
                            if (clickedViewRect.contains(x, y)) {
                                touchTargetIsEditText = true
                                break
                            }
                        }
                    }
                    if (!touchTargetIsEditText) {
                        val imm: InputMethodManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}