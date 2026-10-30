package com.choubapp.practice

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class TaskRepository {

    private val collection = FirebaseFirestore.getInstance().collection("tasks")

    fun getAllTasks(): Flow<List<Task>> = callbackFlow {
        val listener = collection.orderBy("createdAt")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObjects(Task::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    fun insert(task: Task) {
        collection.add(task)
    }

    fun update(task: Task) {
        collection.document(task.id).set(task)
    }

    fun delete(task: Task) {
        collection.document(task.id).delete()
    }
}