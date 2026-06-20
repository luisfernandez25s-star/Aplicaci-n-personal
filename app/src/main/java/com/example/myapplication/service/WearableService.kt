package com.example.myapplication.service

import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.SensorReading
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WearableService : WearableListenerService() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (path == "/sensor_data") {
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val sensorName = dataMap.getString("sensor_name") ?: "Unknown"
                    val value = dataMap.getFloat("value")
                    val timestamp = dataMap.getLong("timestamp")

                    saveToDatabase(sensorName, value, timestamp)
                }
            }
        }
    }

    private fun saveToDatabase(name: String, value: Float, timestamp: Long) {
        serviceScope.launch {
            val database = AppDatabase.getDatabase(applicationContext)
            val reading = SensorReading(sensorName = name, value = value, timestamp = timestamp)
            database.sensorDao().insert(reading)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
