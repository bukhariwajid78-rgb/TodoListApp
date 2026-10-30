package com.choubapp.practice

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Task(
    @DocumentId
    val id: String = "",
    val title: String = "",
    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)