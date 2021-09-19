package com.example.crudmobile.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.crudmobile.models.User

class UserDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val KEY_LOGGED_IN = "loggedInUser"

        /** 1 */
        private const val DATABASE_VERSION = 1
        /** UserDatabase */
        private const val DATABASE_NAME = "UserDatabase"
        /** UserTable */
        private const val TABLE_CONTACTS = "UserTable"

        /** _id */
        private const val KEY_ID = "_id"
        /** name */
        private const val KEY_NAME = "name"
        /** email */
        private const val KEY_EMAIL = "email"
        /** password */
        private const val KEY_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = (
            "CREATE TABLE " + TABLE_CONTACTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT," + KEY_PASSWORD + " TEXT" + ")"
        )

        db?.execSQL(createUsersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }

    /**
     * Add user to [UserDatabase].
     * @param user [User]
     * @return success [Long] or null
     */
    fun addUser(user: User): Long? {
        val db = this.writableDatabase

        return if (findUser(user.email, null) == null) {
            val contentValues = ContentValues()
            contentValues.put(KEY_NAME, user.name.replaceFirstChar { it.uppercase() })
            contentValues.put(KEY_EMAIL, user.email)
            contentValues.put(KEY_PASSWORD, user.password)

            // Inserting employee details using insert query.
            val success = db.insert(TABLE_CONTACTS, null, contentValues)

            db.close()
            success
        } else {
            null
        }
    }

    /**
     * Find user in [UserDatabase]
     * @param email [String]
     * @param password [String]
     * @return void
     */
    fun findUser(email: String, password: String?): User? {

        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS WHERE email = '$email'"

        val db = this.readableDatabase

        val cursor: Cursor?

        var user: User? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                if (password != null) {
                    do {
                        // When password exists
                        if (cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)) == email &&
                            cursor.getString(
                                cursor.getColumnIndexOrThrow(KEY_PASSWORD)) == password
                        ) {

                            user = User(
                                id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)),
                                name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                                email = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)),
                                password = cursor.getString(
                                    cursor.getColumnIndexOrThrow(KEY_PASSWORD)
                                )
                            )

                            cursor.close()
                        }
                    } while (cursor.moveToNext())
                } else {
                    do {
                        // When password is null
                        if (cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)) == email) {

                            user = User(
                                id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)),
                                name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                                email = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)),
                                password = cursor.getString(
                                    cursor.getColumnIndexOrThrow(KEY_PASSWORD)
                                )
                            )

                            cursor.close()
                        }
                    } while (cursor.moveToNext())
                }

            }

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return user
        }

        cursor.close()

        return user
    }

    /**
     * Read users from database in form of [HashSet].
     * @return employeeList [HashSet<EmployeeModel>]
     */
    fun getUsers(): List<User> {

        val userList: HashSet<User> = HashSet()

        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"

        val db = this.readableDatabase
        // Cursor is used to read the record one by one. Add them to data model class.
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    userList.add(User(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                        email = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)),
                        password = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD))
                    ))

                } while (cursor.moveToNext())
            }

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return emptyList()
        }

        cursor.close()

        return userList.sortedBy { e -> e.id }
    }

    /**
     * Update employee
     * @param user [User] to update
     * @return success [Int]
     */
    fun updateUser(user: User): Int {

        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, user.name.replaceFirstChar { it.uppercase() })
        contentValues.put(KEY_EMAIL, user.email)
        contentValues.put(KEY_PASSWORD, user.password)

        // Update
        val success = db.update(
            TABLE_CONTACTS,
            contentValues,
            KEY_ID + "=" + user.id,
            null
        )

        db.close()
        return success
    }

    /**
     * Delete employee
     * @param user [User] to delete
     * @return success [Int]
     */
    fun deleteUser(user: User): Int {
        val db = this.writableDatabase

        // Delete
        val success = db.delete(
            TABLE_CONTACTS,
            KEY_ID + "=" + user.id,
            null
        )

        db.close()
        return success
    }
}