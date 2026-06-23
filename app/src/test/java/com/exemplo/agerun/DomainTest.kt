package com.exemplo.agerun

import com.exemplo.agerun.model.Intensity
import com.exemplo.agerun.model.WorkoutBlock
import com.exemplo.agerun.model.WorkoutEntry
import com.exemplo.agerun.model.WorkoutType
import com.exemplo.agerun.model.totalDistanceKm
import org.junit.Assert.assertEquals
import org.junit.Test

class DomainTest {
    @Test
    fun totalDistanceKm_soma_distancia_dos_blocos() {
        val workout = WorkoutEntry(
            id = "w1",
            title = "Intervalado",
            type = WorkoutType.Intervalado,
            intensity = Intensity.Forte,
            date = "Seg 12",
            focus = "Velocidade",
            targetPace = "4:35/km",
            notes = "",
            blocks = listOf(
                WorkoutBlock("Aquecimento", "10 min trote", distanceKm = 2.0),
                WorkoutBlock("Principal", "6x400m", distanceKm = 3.0),
                WorkoutBlock("Volta à calma", "10 min", distanceKm = 1.5),
            ),
            assignedStudentIds = emptyList(),
        )
        assertEquals(6.5, workout.totalDistanceKm(), 0.001)
    }
}
