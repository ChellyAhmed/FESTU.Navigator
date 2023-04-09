package com.example.festunavigator.data.data_source

import android.util.Log
import com.example.festunavigator.data.model.TreeNodeDto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


class FB_Database {
    private val database = Firebase.database("https://navigationapp-7ae38-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("nodes")
    fun insertNodes(nodes : List<TreeNodeDto>) {
        for (node in nodes) {
            val newNodeRef = database.push()
            newNodeRef.setValue(node)
        }
    }
    suspend fun getNodes():List<TreeNodeDto>{
        val res = database.get().await()
        try {
            val foundNodesMap = res.value as Map<String,TreeNodeDto>
            return ArrayList(foundNodesMap.values)
        }
        catch(e : Exception) {
            return listOf()
        }
    }
    suspend fun getNodesAsMap():Map<String, TreeNodeDto>{
        val res = database.get().await()
        try {
            val foundNodesMap = res.value as Map<String,TreeNodeDto>
            return foundNodesMap
        }
        catch(e : Exception) {
            return mapOf()
        }
    }
    suspend fun deleteNodes(nodes : List<TreeNodeDto>){
        val ids = nodes.map { it.id }
        val refs = mutableListOf<String>()
        val allNodes = getNodesAsMap()
        for(nodeKey in allNodes.keys){
            if(allNodes.get(nodeKey)?.let { ids.contains(it.id) } == true) {
                if (nodeKey != null) {
                    refs.add(nodeKey)
                }
            }
        }
        for (ref in refs){
            database.child(ref).removeValue();
        }
    }
    fun updateNodes(nodes : List<TreeNodeDto>) {
        for (node in nodes) {
            val newNodeRef = database.push()
            newNodeRef.setValue(node)
        }
    }

    fun clearNodes(){
        database.removeValue();
    }
}