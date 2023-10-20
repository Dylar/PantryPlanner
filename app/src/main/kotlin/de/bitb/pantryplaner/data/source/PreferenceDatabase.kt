package de.bitb.pantryplaner.data.source

import android.content.SharedPreferences

class PreferenceDatabase(private val pref: SharedPreferences) : LocalDatabase {
    override fun setUser(uuid: String) = pref.edit().putString("user", uuid).apply()
    override fun getUser(): String = pref.getString("user", "")!!
}