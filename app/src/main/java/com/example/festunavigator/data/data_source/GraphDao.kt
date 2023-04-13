package com.example.festunavigator.data.data_source

import android.util.Log
import androidx.room.*
import com.example.festunavigator.data.model.TreeNodeDto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

@Dao
interface GraphDao {

    @Query("SELECT * FROM treenodedto")
    fun getNodes(): List<TreeNodeDto>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNodes(nodes: List<TreeNodeDto>)

    @Delete
    fun deleteNodes(nodes: List<TreeNodeDto>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateNodes(nodes: List<TreeNodeDto>)

    @Query("DELETE FROM treenodedto")
    fun clearNodes()

    fun pushNodesToFirebase(){
        val database = Firebase.database("https://navigationapp-7ae38-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("nodes")
        database.removeValue()
        val localNodes = getNodes()
        if (localNodes != null) {
            for (node in localNodes){
                val newNodeRef = database.push()
                newNodeRef.setValue(
                    TreeNodeDto( node.id , node.x, node.y, node.z, node.type, node.number?:null, node.neighbours?:mutableListOf() , node.forwardVector?:null)
                )
            }
        }
    }
    suspend fun getNodesFromFirebase(){
        val database = Firebase.database("https://navigationapp-7ae38-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("nodes")
        val res = database.get().await()
        try {
            val foundNodesMap = res.value as Map<String,TreeNodeDto>
            val foundNodes = ArrayList(foundNodesMap.values)
            updateNodes(foundNodes)
        }
        catch(e : Exception) {
            Log.e("DB_LOG",e.toString())
        }
    }


}