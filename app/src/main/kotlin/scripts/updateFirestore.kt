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

//    val fireData = FirebaseFirestore.getInstance()
//    runBlocking {
//        val collection = fireData.collection("stage/prod/items")
//        fireData.batch().apply {
//            collection
//                .get().await().documents
//                .forEach { snap ->
//                    val ref = snap.reference
//                    ref.snapshots().collect {
//                        if (it.data != null) {
//                            if (it.data!!["uuid"] == null) {
//                                ref.update(mapOf("uuid" to UUID.randomUUID().toString()))
//                            }
//                        }
//                    }
//                }
//            commit()
//        }
//    }

}
