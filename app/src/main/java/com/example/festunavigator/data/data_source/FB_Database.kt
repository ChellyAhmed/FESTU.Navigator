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
            Log.e("TAG Insert",node.toString())
            newNodeRef.setValue(TreeNodeDto( node.id , node.x, node.y, node.z, node.type, node.number?:null))
        }
    }
    suspend fun getNodes(): List<TreeNodeDto> {
        val res = database.get().await()
        try {
            val foundNodesMap = res.value as Map<String, TreeNodeDto>
            Log.e("ZZZ", "Found nodes:" + foundNodesMap.toString())
            var arrList = ArrayList(foundNodesMap.values)
            arrList.map {
                x ->
                (x.neighbours?:null)?.let {
                    TreeNodeDto(x.id, x.x, x.y, x.z, x.type,
                        x.number?:null, it, x.forwardVector?:null)
                }
            }
            Log.e("ZZZ", "Exce")
            return arrList
        } catch(e: Exception) {
            Log.e("ZZZ", "Exception caught finding nodes: " + e.toString())
            return emptyList()
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
            Log.d("ZZZ", "Update")
            newNodeRef.setValue(TreeNodeDto( node.id , node.x, node.y, node.z, node.type, node.number?:null, node.neighbours?:mutableListOf() , node.forwardVector?:null))
        }
    }

    fun clearNodes(){
        database.removeValue();
    }
}