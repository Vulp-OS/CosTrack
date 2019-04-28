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

    fun deleteCoverImage(imagePath: String, parentType: Int, parentID: String): Boolean {
        var successful = true
        var collectionName = ""
        var defaultImageURL = ""

        if (parentType == 0) {
            collectionName = "cosplays"
            defaultImageURL = "https://firebasestorage.googleapis.com/v0/b/costrack.appspot.com/o/defaults%2Fnew-cosplay.png?alt=media&token=8bcf7371-7e6d-476a-b33a-9e7e3afce1c8"
        } else if (parentType == 1) {
            collectionName = "components"
            defaultImageURL = "https://firebasestorage.googleapis.com/v0/b/costrack.appspot.com/o/defaults%2Fnew-component.png?alt=media&token=16721b21-7d3e-4326-bb38-20e0aeccf101"
        }

        try {
            db.collection(collectionName).document(parentID).update("cover_image", defaultImageURL)
            storage.getReferenceFromUrl(imagePath).delete()
        } catch (e: Exception) {
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

                    deleteCoverImage(it.data!!["cover_image"] as String, 1, componentID)

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

                    deleteCoverImage(it.data!!["cover_image"] as String, 0, cosplayID)

                    db.collection("cosplays").document(cosplayID).delete()
                }
            }
        } catch (e: Exception) {
            successful = false
        }

        return successful
    }
}