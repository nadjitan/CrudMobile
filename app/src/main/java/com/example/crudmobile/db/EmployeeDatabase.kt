package com.example.crudmobile.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.crudmobile.models.Employee

class EmployeeDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        /** 1 */
        private const val DATABASE_VERSION = 1
        /** EmployeeDatabase */
        private const val DATABASE_NAME = "EmployeeDatabase"
        /** EmployeeTable */
        private const val TABLE_CONTACTS = "EmployeeTable"

        /** _id */
        private const val KEY_ID = "_id"
        /** name */
        private const val KEY_NAME = "name"
        /** email */
        private const val KEY_EMAIL = "email"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createEmployeesTable = (
            "CREATE TABLE " + TABLE_CONTACTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT" + ")"
        )

        db?.execSQL(createEmployeesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }

    /**
     * Add employee to [EmployeeDatabase].
     * @param employee [Employee]
     * @return success [Long]
     */
    fun addEmployee(employee: Employee): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, employee.name.replaceFirstChar { it.uppercase() })
        contentValues.put(KEY_EMAIL, employee.email)

        // Inserting employee details using insert query.
        val success = db.insert(TABLE_CONTACTS, null, contentValues)

        db.close()
        return success
    }

    /**
     * Add employee to [EmployeeDatabase].
     * @param employees [HashSet]
     * @return success [Long]
     */
    fun addEmployees(employees: HashSet<Employee>): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        var success: Long = -1

        for (employee in employees) {
            contentValues.put(KEY_NAME, employee.name.replaceFirstChar { it.uppercase() })
            contentValues.put(KEY_EMAIL, employee.email)

            // Inserting employee details using insert query.
            success = db.insert(TABLE_CONTACTS, null, contentValues)
        }

        db.close()
        return success
    }

    /**
     * Read employees from database in form of [HashSet].
     * @return employeeList [List<Employee>]
     */
    fun getEmployees(): List<Employee> {

        val employeeList: HashSet<Employee> = HashSet()

        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"

        val db = this.readableDatabase
        // Cursor is used to read the record one by one. Add them to data model class.
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    employeeList.add(Employee(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                        email = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL))
                    ))

                } while (cursor.moveToNext())
            }

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return emptyList()
        }

        cursor.close()

        // For immediate population purposes
        if (employeeList.size < 1) {
            val employees = HashSet<Employee>()

            employees.add(Employee(1, "John", "john@email.com"))
            employees.add(Employee(2, "Mark", "mark@email.com"))
            employees.add(Employee(3, "Elise", "elise@email.com"))
            employees.add(Employee(4, "Jane", "jane@email.com"))
            employees.add(Employee(5, "Juan", "juan@email.com"))
            employees.add(Employee(6, "Noel", "noel@email.com"))
            employees.add(Employee(7, "Tim", "tim@email.com"))
            employees.add(Employee(8, "Carl", "carl@email.com"))

            addEmployees(employees)
            return getEmployees().sortedBy { e -> e.id }
        }

        return employeeList.sortedBy { e -> e.id }
    }

    /**
     * Update employee.
     * @param employee Employee to update.
     * @return success [Int]
     */
    fun updateEmployee(employee: Employee): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, employee.name.replaceFirstChar { it.uppercase() })
        contentValues.put(KEY_EMAIL, employee.email)

        // Update
        val success = db.update(
            TABLE_CONTACTS,
            contentValues,
            KEY_ID + "=" + employee.id,
            null
        )

        db.close()
        return success
    }

    /**
     * Delete employee.
     * @param employee Employee to delete.
     * @return success [Int]
     */
    fun deleteEmployee(employee: Employee): Int {
        val db = this.writableDatabase

        // Delete
        val success = db.delete(
            TABLE_CONTACTS,
            KEY_ID + "=" + employee.id,
            null
        )

        db.close()
        return success
    }
}