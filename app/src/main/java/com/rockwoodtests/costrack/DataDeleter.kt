package com.rockwoodtests.costrack

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class DataDeleter {
    private var db = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()

    fun deleteReferenceImage(imagePath: String, parentType: Int, parentID: String): Boolean {
        var successful = true
        var collectionName = ""

        if (parentType == 0) {
            collectionName = "cosplays"
        } else if (parentType == 1) {
            collectionName = "components"
        }

        try {
            db.collection(collectionName).document(parentID).update("references", FieldValue.arrayRemove(imagePath))
            storage.getReferenceFromUrl(imagePath).delete()
        } catch(e: Exception) {
            successful = false
        }

        return successful
    }

    fun deleteComponent(componentID: String, cosplayID: String): Boolean {
        var successful = true

        try {
            db.collection("components").document(componentID).get().addOnSuccessListener {
                if (it != null && it.data != null) {
                    val references = it.data!!["references"] as ArrayList<*>

                    for (reference in references) {
                        deleteReferenceImage(reference as String, 1, componentID)
                    }

                    db.collection("cosplays").document(cosplayID).update("components", FieldValue.arrayRemove(componentID))
                    db.collection("components").document(componentID).delete()
                }
            }
        } catch (e: Exception) {
            successful = false
        }

        return successful
    }

    fun deleteCosplay(cosplayID: String): Boolean {
        var successful = true

        try {
            db.collection("cosplays").document(cosplayID).get().addOnSuccessListener {
                if (it != null && it.data != null) {
                    val components = it.data!!["components"] as ArrayList<*>

                    for (component in components) {
                        deleteComponent(component as String, cosplayID)
                    }

                    val references = it.data!!["references"] as ArrayList<*>

                    for (reference in references) {
                        deleteReferenceImage(reference as String, 0, cosplayID)
                    }

                    db.collection("cosplays").document(cosplayID).delete()
                }
            }
        } catch (e: Exception) {
            successful = false
        }

        return successful
    }
}