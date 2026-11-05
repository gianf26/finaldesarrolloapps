package ar.edu.examen.iiot

import ar.edu.examen.iiot.model.PlantRules
import ar.edu.examen.iiot.model.Reading
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlantRulesTest {
    private fun sample(temp: Double) = Reading(
        machineId = "TEST-01",
        machineName = "Equipo de prueba",
        temperature = temp,
        humidity = 50.0,
        vibration = 1.0,
        timestamp = 1L,
        source = "Test"
    )

    @Test fun belowThresholdIsNormal() {
        assertFalse(PlantRules.requiresAttention(sample(64.9)))
    }

    @Test fun thresholdIsAlert() {
        assertTrue(PlantRules.requiresAttention(sample(65.0)))
    }
}
