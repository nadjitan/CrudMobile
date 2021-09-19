package com.example.crudmobile.ui.main

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crudmobile.*
import com.example.crudmobile.adapters.EmployeeAdapter
import com.example.crudmobile.controllers.MainActivity.Companion.getLoggedInUser
import com.example.crudmobile.controllers.MainActivity.Companion.logout
import com.example.crudmobile.databinding.FragmentMainBinding
import com.example.crudmobile.db.EmployeeDatabase
import com.example.crudmobile.models.Employee
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_update.*
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(R.layout.fragment_main) {

    private val employeeAdapter by lazy { EmployeeAdapter(this, requireContext()) }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    /**
     * Function is used to get the Items List which is added in the database table.
     * @return employeeList - type of [HashSet]
     */
    private fun getDbEmployees(): List<Employee> {
        // Instance of DatabaseHandler class
        val databaseHandler = EmployeeDatabase(requireContext())
        // Return employeeList
        return databaseHandler.getEmployees()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        val loggedInUser = getLoggedInUser(requireActivity())
        if (loggedInUser != null) {
            "Hello ${loggedInUser.name}!".also { binding.tvLoggedInUser.text = it }
        } else {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragment2ToLoginFragment()
            )
        }

        binding.btnLogout.setOnClickListener {
            logout(requireActivity())
            findNavController().navigate(
                MainFragmentDirections.actionMainFragment2ToLoginFragment()
            )
        }

        binding.btnAdd.setOnClickListener { saveEmployee() }

        // Populate employees RecyclerView
        val employees = getDbEmployees()
        if (employees.isNotEmpty()) {
            employeeAdapter.setData(employees)

            binding.searchEmployee.visibility = View.VISIBLE
            binding.rvItemsList.visibility = View.VISIBLE
            binding.tvNoRecordsAvailable.visibility = View.GONE

            binding.rvItemsList.layoutManager = LinearLayoutManager(requireContext())
            binding.rvItemsList.adapter = employeeAdapter
        } else {
            binding.searchEmployee.visibility = View.GONE
            binding.rvItemsList.visibility = View.GONE
            binding.tvNoRecordsAvailable.visibility = View.VISIBLE
        }
        // Employees SearchView
        binding.searchEmployee.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                employeeAdapter.getFilter().filter(query)
                return false
            }
        })

        return binding.root
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

                employeeAdapter.setData(getDbEmployees())
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
     * Update toast dialog.
     * @param employee Type of [Employee]
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

                    employeeAdapter.setData(getDbEmployees())

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
     * @param employee Type of [Employee]
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

                employeeAdapter.setData(getDbEmployees())
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