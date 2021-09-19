package com.example.crudmobile.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.crudmobile.models.Employee

class EmployeeDiffUtil(
    private val oldEmployeeList: List<Employee>,
    private val newEmployeeList: List<Employee>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int { return oldEmployeeList.size }

    override fun getNewListSize(): Int { return newEmployeeList.size }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldEmployeeList[oldItemPosition].id == newEmployeeList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldEmployeeList[oldItemPosition].id != newEmployeeList[newItemPosition].id -> false
            oldEmployeeList[oldItemPosition].name != newEmployeeList[newItemPosition].name -> false
            oldEmployeeList[oldItemPosition].email != newEmployeeList[newItemPosition].email -> false
            else -> true
        }
    }
}