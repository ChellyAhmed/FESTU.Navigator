package com.example.festunavigator.data.data_source

import com.example.festunavigator.data.model.TreeNodeDto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class FB_Database {
    private val database = Firebase.database.reference.child("nodes")
    fun insertNodes(nodes : List<TreeNodeDto>) {
        for (node in nodes) {
            val newNodeRef = database.push()
            newNodeRef.setValue(node)
        }
    }
    fun getNodes():List<TreeNodeDto>{
        var nodes = listOf<TreeNodeDto>()
        database.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var foundNodes = dataSnapshot.value as Map<String?, TreeNodeDto?>
                    nodes = foundNodes.values.toList() as List<TreeNodeDto>
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        while(nodes.isEmpty()) {}
        return nodes
    }
    fun getNodesAsMap():Map<String?, TreeNodeDto?>{
        var nodes = mapOf<String?,TreeNodeDto?>()
        database.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var foundNodes = dataSnapshot.value as Map<String?, TreeNodeDto?>
                    nodes = foundNodes
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        while(nodes.isEmpty()) {}
        return nodes
    }
    fun deleteNodes(nodes : List<TreeNodeDto>){
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