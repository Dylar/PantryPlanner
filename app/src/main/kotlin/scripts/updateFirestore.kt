package scripts

import com.google.firebase.firestore.FirebaseFirestore
import de.bitb.pantryplaner.data.model.CheckItem
import kotlinx.coroutines.tasks.await

suspend fun main() {
    val firestore = FirebaseFirestore.getInstance()
    val collection = firestore.collection("stage/prod/checklists")
    firestore.batch().apply {
        collection
            .get().await().documents
            .forEach { snap ->
                val items: List<String> = (snap.data?.get("items") as? List<String>) ?: listOf()
                snap.reference.update(mapOf("items2" to items.map { CheckItem(it) }))
            }
        commit()
    }

}
