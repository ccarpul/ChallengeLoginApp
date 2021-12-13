package com.intercorp.android.challenge.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.intercorp.android.challenge.presentation.FormDataUserFragment

class LoginRepository {

    fun saveData(uid: String, data: Map<String, String>): Task<*> {
        val firestone = FirebaseFirestore
            .getInstance()
            .document("${FormDataUserFragment.COLLECTION_FIRESTONE_USERS}$uid")
        return firestone.set(data)
    }
}
