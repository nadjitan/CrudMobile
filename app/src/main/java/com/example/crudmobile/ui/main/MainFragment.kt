package com.example.crudmobile.ui.main

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crudmobile.*
import com.example.crudmobile.controllers.MainActivity.Companion.logout
import com.example.crudmobile.db.EmployeeDatabase
import com.example.crudmobile.db.UserDatabase.Companion.KEY_LOGGED_IN
import com.example.crudmobile.models.Employee
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_update.*
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment(R.layout.main_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return

        tvLoggedInUser.text = sharedPref.getString(KEY_LOGGED_IN, "User not found")

        btnAdd.setOnClickListener { saveEmployee() }

        putDataIntoRecyclerView()

        btnLogout.setOnClickListener {
            logout(activity)
            findNavController().navigate(
                MainFragmentDirections.actionMainFragment2ToLoginFragment()
            )
        }
    }

    /**
     * Save employee to database
     * @return void
     */
    private fun saveEmployee() {
        val name = etName.text.toString()
        val email = etEmailId.text.toString()
        val databaseHandler = EmployeeDatabase(requireContext())

        if (name.isNotEmpty() && email.isNotEmpty()) {
            val status = databaseHandler.addEmployee(Employee(0, name, email))

            if (status > -1) {
                Snackbar.make(
                    mainFragment,
                    "Employee saved.",
                    Snackbar.LENGTH_SHORT
                ).show()

                etName.text?.clear()
                etEmailId.text?.clear()

                putDataIntoRecyclerView()
            }
        } else {
            Snackbar.make(
                mainFragment,
                "Name or Email cannot be blank.",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Function is used to get the Items List which is added in the database table.
     * @return employeeList
     */
    private fun getItemsList(): ArrayList<Employee> {
        // Instance of DatabaseHandler class
        val databaseHandler = EmployeeDatabase(requireContext())
        // Return employeeList
        return databaseHandler.viewEmployee()
    }

    /**
     * Puts data into the [RecyclerView].
     * @return void
     */
    private fun putDataIntoRecyclerView() {
        if (getItemsList().size > 0) {
            rvItemsList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE

            // Set the LayoutManager that this RecyclerView will use.
            rvItemsList.layoutManager = LinearLayoutManager(requireContext())
            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = ItemAdapter(requireContext(), getItemsList(), this)
            // adapter instance is set to the recyclerview to inflate the items.
            rvItemsList.adapter = itemAdapter

        } else {
            rvItemsList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }

    /**
     * Update toast dialog.
     * @param employee Employee details.
     * @return void
     */
    fun updateEmployeeDialog(employee: Employee) {
        val updateDialog = Dialog(requireContext(), R.style.Theme_Update_Dialog)

        updateDialog.setCancelable(false)
        updateDialog.setContentView(R.layout.dialog_update) // Set to custom xml layout
        updateDialog.etUpdateName.setText(employee.name) // Set EditText to name
        updateDialog.etUpdateEmailId.setText(employee.email) // Set EditText to email

        updateDialog.tvUpdate.setOnClickListener {
            val name = updateDialog.etUpdateName.text.toString()
            val email = updateDialog.etUpdateEmailId.text.toString()

            val databaseHandler = EmployeeDatabase(requireContext())

            if (name.isNotEmpty() && email.isNotEmpty()) {
                val status = databaseHandler.updateEmployee(
                    Employee(employee.id, name, email)
                )
                if (status > -1) {
                    Snackbar.make(
                        mainFragment,
                        "Employee updated.",
                        Snackbar.LENGTH_SHORT
                    ).show()

                    putDataIntoRecyclerView()

                    updateDialog.dismiss() // Dialog close
                }
            } else {
                Snackbar.make(
                    mainFragment,
                    "Name or Email cannot be blank.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        updateDialog.tvCancel.setOnClickListener {
            updateDialog.dismiss()
        }

        // Start the dialog and display it on screen.
        updateDialog.show()
    }

    /**
     * Delete alert dialog.
     * @param employee Employee details.
     * @return void
     */
    fun deleteEmployeeAlertDialog(employee: Employee) {
        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setTitle("DELETE RECORD")
            .setMessage("Are you sure you want to delete ${employee.name}.")

        // Performing positive action
        builder.setNegativeButton("DELETE") { dialogInterface, _ ->

            val databaseHandler = EmployeeDatabase(requireContext())

            val status = databaseHandler.deleteEmployee(
                Employee(employee.id, "", "")
            )

            if (status > -1) {
                Snackbar.make(
                    mainFragment,
                    "${employee.name}'s record deleted.",
                    Snackbar.LENGTH_SHORT
                ).show()

                putDataIntoRecyclerView()
            }

            dialogInterface.dismiss() // Dialog close
        }

        // Performing negative action
        builder.setPositiveButton("CANCEL") { dialogInterface, _ ->
            dialogInterface.dismiss() // Dialog close
        }

        val alertDialog: AlertDialog = builder.create()

        // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.setCancelable(false)

        alertDialog.show()
    }
}