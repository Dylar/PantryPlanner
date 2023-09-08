package de.bitb.pantryplaner.data.model

import com.google.firebase.firestore.Exclude

data class User(
    val uuid: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val connectedUser: MutableList<String> = mutableListOf(),
) {
    @get:Exclude
    val fullName: String
        get() = "$firstName $lastName"
}