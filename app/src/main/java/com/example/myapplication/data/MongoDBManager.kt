package com.example.myapplication.data

import com.mongodb.kotlin.client.coroutine.MongoClient
import org.bson.Document
import android.util.Log

class MongoDBManager {
    private val connectionString = "mongodb+srv://luisg:G3r0n1m0202005@escuela.tjez8ct.mongodb.net/?appName=Escuela"
    private val client = MongoClient.create(connectionString)
    private val database = client.getDatabase("Escuela")
    private val collection = database.getCollection<Document>("LecturasSensores")

    suspend fun saveReading(reading: SensorReading) {
        try {
            val doc = Document()
                .append("sensorName", reading.sensorName)
                .append("value", reading.value.toDouble()) // Mongo prefiere Double
                .append("timestamp", reading.timestamp)
            
            collection.insertOne(doc)
            Log.d("MongoDBManager", "Dato enviado exitosamente a MongoDB Atlas")
        } catch (e: Exception) {
            Log.e("MongoDBManager", "Error al conectar o insertar en MongoDB Atlas. Verifica tu IP en el dashboard de Atlas.", e)
        }
    }
    
    companion object {
        @Volatile
        private var INSTANCE: MongoDBManager? = null

        fun getInstance(): MongoDBManager {
            return INSTANCE ?: synchronized(this) {
                val instance = MongoDBManager()
                INSTANCE = instance
                instance
            }
        }
    }
}
