package ar.edu.examen.iiot.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ar.edu.examen.iiot.model.Reading
import ar.edu.examen.iiot.model.PlantRules

class PlantDatabase(context: Context) :
    SQLiteOpenHelper(context, "planta_iiot.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE readings (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                machine_id TEXT NOT NULL,
                machine_name TEXT NOT NULL,
                temperature REAL NOT NULL,
                humidity REAL NOT NULL,
                vibration REAL NOT NULL,
                captured_at INTEGER NOT NULL,
                source TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            "CREATE TABLE configuration (config_key TEXT PRIMARY KEY, config_value TEXT NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // No borrar tablas: las futuras modificaciones de esquema
        // deben implementarse mediante migraciones que preserven los datos.
    }

    fun insert(reading: Reading) {
        val values = ContentValues().apply {
            put("machine_id", reading.machineId)
            put("machine_name", reading.machineName)
            put("temperature", reading.temperature)
            put("humidity", reading.humidity)
            put("vibration", reading.vibration)
            put("captured_at", reading.timestamp)
            put("source", reading.source)
        }

        writableDatabase.insertOrThrow("readings", null, values)
    }

    fun saveThreshold(value: Double) {
        val values = ContentValues().apply {
            put("config_key", "temperature_threshold")
            put("config_value", value.toString())
        }

        writableDatabase.insertWithOnConflict(
            "configuration",
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun getThreshold(): Double {
        val cursor = readableDatabase.rawQuery(
            "SELECT config_value FROM configuration WHERE config_key = ?",
            arrayOf("temperature_threshold")
        )

        cursor.use {
            if (it.moveToFirst()) {
                return it.getString(0).toDoubleOrNull()
                    ?: PlantRules.DEFAULT_TEMPERATURE_LIMIT
            }
        }

        saveThreshold(PlantRules.DEFAULT_TEMPERATURE_LIMIT)
        return PlantRules.DEFAULT_TEMPERATURE_LIMIT
    }

    fun latest(): List<Reading> {
        val sql = """
            SELECT r.id, r.machine_id, r.machine_name,
                   r.temperature, r.humidity, r.vibration,
                   r.captured_at, r.source
            FROM readings r
            INNER JOIN (
                SELECT machine_id, MAX(id) AS latest_id
                FROM readings
                GROUP BY machine_id
            ) x ON r.id = x.latest_id
            ORDER BY r.machine_name
        """.trimIndent()

        return readableDatabase.rawQuery(sql, null).use(::readAll)
    }

    fun findLatestByName(filter: String): List<Reading> {
        val sql = """
            SELECT r.id, r.machine_id, r.machine_name,
                   r.temperature, r.humidity, r.vibration,
                   r.captured_at, r.source
            FROM readings r
            INNER JOIN (
                SELECT machine_id, MAX(id) AS latest_id
                FROM readings
                GROUP BY machine_id
            ) x ON r.id = x.latest_id
            WHERE r.machine_name LIKE ?
            ORDER BY r.machine_name
        """.trimIndent()

        return readableDatabase.rawQuery(
            sql,
            arrayOf("%$filter%")
        ).use(::readAll)
    }

    private fun readAll(cursor: Cursor): List<Reading> {
        val items = mutableListOf<Reading>()

        while (cursor.moveToNext()) {
            items += Reading(
                id = cursor.getLong(0),
                machineId = cursor.getString(1),
                machineName = cursor.getString(2),
                temperature = cursor.getDouble(3),
                humidity = cursor.getDouble(4),
                vibration = cursor.getDouble(5),
                timestamp = cursor.getLong(6),
                source = cursor.getString(7)
            )
        }

        return items
    }

    fun history(machineId: String): List<Reading> {
        val sql = """
            SELECT id, machine_id, machine_name,
                   temperature, humidity, vibration,
                   captured_at, source
            FROM readings
            WHERE machine_id = ?
            ORDER BY captured_at DESC, id DESC
            LIMIT 10
        """.trimIndent()

        return readableDatabase.rawQuery(
            sql,
            arrayOf(machineId)
        ).use(::readAll)
    }
}