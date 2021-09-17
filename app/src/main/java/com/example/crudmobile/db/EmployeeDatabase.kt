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
     * Read employees from database in form of [ArrayList].
     * @return employeeList [ArrayList<EmployeeModel>]
     */
    fun viewEmployee(): ArrayList<Employee> {

        val employeeList: ArrayList<Employee> = ArrayList()

        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"

        val db = this.readableDatabase
        // Cursor is used to read the record one by one. Add them to data model class.
        val cursor: Cursor?

        var id: Long
        var name: String
        var email: String

        try {
            cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID))
                    name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME))
                    email = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL))

                    val employee = Employee(id = id, name = name, email = email)
                    employeeList.add(employee)

                } while (cursor.moveToNext())
            }

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        cursor.close()

        return employeeList
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

        val contentValues = ContentValues()
        contentValues.put(KEY_ID, employee.id) //

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