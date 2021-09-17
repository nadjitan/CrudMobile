package com.example.crudmobile.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.crudmobile.R
import com.example.crudmobile.db.UserDatabase
import com.example.crudmobile.models.User
import com.example.crudmobile.utils.Validator.Companion.isValidEmail
import com.example.crudmobile.utils.Validator.Companion.isValidPassword
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_signup.*

class SignupFragment : Fragment(R.layout.fragment_signup) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvGoToLogin.setOnClickListener {
            findNavController().navigate(
                SignupFragmentDirections.actionSignupFragmentToLoginFragment()
            )
        }

        btnSignup.setOnClickListener {
            signup()
        }
    }

    /**
     * Create user.
     * @return void
     */
    private fun signup() {
        val name = etSignupName.text.toString()
        val email = etSignupEmail.text.toString()
        val password = etSignupPassword.text.toString()
        val confirmPassword = etSignupConfirmPassword.text.toString()
        val databaseHandler = UserDatabase(requireContext())

        // Empty check
        if (name.isNotEmpty() && email.isNotEmpty() &&
            password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            // Email and Password validation
            if (isValidEmail(etSignupEmail) && isValidPassword(etSignupPassword)) {
                // Check if Password match
                if (password == confirmPassword) {
                    val status = databaseHandler.addUser(User(0, name, email, password))

                    if (status != null) {
                        if (status > -1) {
                            // User gets created
                            etSignupName.text?.clear()
                            etSignupEmail.text?.clear()
                            etSignupPassword.text?.clear()
                            etSignupConfirmPassword.text?.clear()

                            findNavController().navigate(
                                SignupFragmentDirections.actionSignupFragmentToLoginFragment()
                            )

                            Snackbar.make(
                                signupFragment,
                                "User successfully created!.",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }   else {
                        Snackbar.make(
                            signupFragment,
                            "User already exists.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Snackbar.make(
                        signupFragment,
                        "Password does not match.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Snackbar.make(
                signupFragment,
                "Email, name, and password must not be empty.",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}