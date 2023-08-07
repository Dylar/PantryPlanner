package de.bitb.pantryplaner.data.model

data class User(
    val uuid: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
) {
    val fullName: String
        get() = "$firstName $lastName"
}