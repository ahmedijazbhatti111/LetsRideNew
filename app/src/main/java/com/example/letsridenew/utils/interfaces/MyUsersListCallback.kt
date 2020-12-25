package com.example.letsridenew.utils.interfaces

import com.example.letsridenew.models.User
import java.util.*

interface MyUsersListCallback {
    fun onUsersRead(users: ArrayList<User?>?)
}