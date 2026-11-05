package ar.edu.examen.iiot.model

data class Reading(
    val id: Long = 0,
    val machineId: String,
    val machineName: String,
    val temperature: Double,
    val humidity: Double,
    val vibration: Double,
    val timestamp: Long,
    val source: String
)

object PlantRules {
    const val DEFAULT_TEMPERATURE_LIMIT = 65.0

    fun requiresAttention(reading: Reading): Boolean =
        reading.temperature >= DEFAULT_TEMPERATURE_LIMIT
}
