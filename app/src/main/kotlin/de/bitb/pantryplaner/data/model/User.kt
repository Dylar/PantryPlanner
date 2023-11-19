package de.bitb.pantryplaner.data.model

import com.google.firebase.firestore.Exclude
import java.util.UUID

data class User(
    val uuid: String = UUID.randomUUID().toString(),
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val connectedUser: List<String> = listOf(),
) {
    @get:Exclude
    val fullName: String
        get() = "$firstName $lastName"
}