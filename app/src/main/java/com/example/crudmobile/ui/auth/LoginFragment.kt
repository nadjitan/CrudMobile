package com.example.crudmobile.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.crudmobile.R
import com.example.crudmobile.controllers.MainActivity.Companion.setLoggedInUser
import com.example.crudmobile.db.UserDatabase
import com.example.crudmobile.utils.Validator.Companion.isValidEmail
import com.example.crudmobile.utils.Validator.Companion.isValidPassword
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvGoToSignup.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToSignupFragment()
            )
        }

        btnLogin.setOnClickListener {
            login()
        }
    }

    /**
     * Login user.
     * @return void
     */
    private fun login() {
        val email = etLoginEmail.text.toString().trim()
        val password = etLoginPassword.text.toString().trim()

        val databaseHandler = UserDatabase(requireContext())

        // Empty check
        if (email.isNotEmpty() && password.isNotEmpty()) {
            // Email and Password validation
            if (isValidEmail(etLoginEmail) && isValidPassword(etLoginPassword)) {

                val status = databaseHandler.findUser(email, password)

                if (status != null) {
                    setLoggedInUser(requireActivity(), status)

                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToPagerFragment()
                    )
                } else {
                    Snackbar.make(
                        loginFragment,
                        "User not found.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Snackbar.make(
                loginFragment,
                "Email and password must not be empty.",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}