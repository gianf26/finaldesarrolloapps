package ar.edu.examen.iiot.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ar.edu.examen.iiot.data.PlantRepository
import ar.edu.examen.iiot.model.PlantRules
import ar.edu.examen.iiot.model.Reading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardState(
    val readings: List<Reading> = emptyList(),
    val filter: String = "",
    val loading: Boolean = false,
    val syncMessage: String = "Lecturas locales",
    val notification: String? = null,
    val threshold: Double = PlantRules.DEFAULT_TEMPERATURE_LIMIT,
    val alertCount: Int = 0,
    val selectedMachineId: String? = null,
    val historyReadings: List<Reading> = emptyList()
)

class PlantViewModel(private val repository: PlantRepository) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        val threshold = repository.getThreshold()
        _state.update { it.copy(threshold = threshold) }

        val data = repository.localReadings()
        setReadings(data)
    }

    private fun setReadings(items: List<Reading>) {
        _state.update { current ->
            current.copy(
                readings = items,
                alertCount = items.count {
                    it.temperature >= current.threshold
                }
            )
        }
    }

    fun refresh() {
        if (_state.value.loading) return
        viewModelScope.launch {
            _state.update { it.copy(loading = true, notification = null) }
            try {
                repository.sync()
                val items = repository.search(_state.value.filter)
                setReadings(items)
                _state.update { it.copy(syncMessage = repository.lastSyncMessage) }
            } catch (exception: Exception) {
                _state.update { it.copy(syncMessage = "No se pudo actualizar la telemetría") }
            } finally {
                _state.update { it.copy(loading = false) }
            }
        }
    }

    fun filter(text: String) {
        _state.update { it.copy(filter = text) }
        try {
            setReadings(repository.search(text))
        } catch (exception: Exception) {
            _state.update {
                it.copy(notification = "La búsqueda no se pudo completar")
            }
        }
    }

    fun configureThreshold(input: String) {
        val value = input.toDoubleOrNull()

        if (value == null || value !in 30.0..120.0) {
            _state.update {
                it.copy(
                    notification = "Ingresá un valor numérico entre 30 y 120 °C."
                )
            }
            return
        }

        try {
            repository.saveThreshold(value)

            _state.update {
                it.copy(
                    threshold = value,
                    alertCount = it.readings.count {
                            reading -> reading.temperature >= value
                    },
                    notification = "Umbral actualizado a $value °C."
                )
            }
        } catch (exception: Exception) {
            _state.update {
                it.copy(notification = "No se pudo guardar el umbral.")
            }
        }
    }

    fun showHistory(machineId: String) {
        try {
            val history = repository.history(machineId)

            _state.update {
                it.copy(
                    selectedMachineId = machineId,
                    historyReadings = history,
                    notification = null
                )
            }
        } catch (exception: Exception) {
            _state.update {
                it.copy(
                    notification = "No se pudo cargar el historial."
                )
            }
        }
    }

    fun closeHistory() {
        _state.update {
            it.copy(
                selectedMachineId = null,
                historyReadings = emptyList()
            )
        }
    }

    fun dismissNotification() {
        _state.update { it.copy(notification = null) }
    }

    class Factory(private val repository: PlantRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(PlantViewModel::class.java))
            return PlantViewModel(repository) as T
        }
    }
}
