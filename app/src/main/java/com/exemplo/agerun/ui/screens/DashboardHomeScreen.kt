package com.exemplo.agerun.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.exemplo.agerun.model.LocationPoint
import com.exemplo.agerun.ui.components.HeroSection
import com.exemplo.agerun.ui.components.LiveMapCard
import com.exemplo.agerun.ui.components.SectionTitle
import com.exemplo.agerun.ui.components.StatusMiniCard
import com.exemplo.agerun.ui.components.WorkoutCard
import com.exemplo.agerun.state.AgeRunAppState

@Composable
fun DashboardHomeScreen(
    appState: AgeRunAppState,
    currentLocation: LocationPoint?,
    hasLocationPermission: Boolean,
    onRequestLocationPermission: () -> Unit,
) {
    val pendingPayments = appState.students.flatMap { student ->
        student.payments
            .filter { it.status != "Pago" }
            .map { payment -> student.name to payment }
    }.take(3)

    HeroSection(
        title = "Painel do professor",
        subtitle = "Indicadores, comunicados e acompanhamento da turma em um fluxo direto.",
    )
    Spacer(modifier = Modifier.height(20.dp))
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        appState.dashboardMetrics.forEach { metric ->
            StatusMiniCard(
                modifier = Modifier.fillMaxWidth(),
                title = metric.label,
                value = metric.value,
            )
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
    SectionTitle("Treinos desta semana")
    Spacer(modifier = Modifier.height(14.dp))
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        appState.workoutDays.forEach { workout ->
            WorkoutCard(day = workout)
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
    LiveMapCard(
        hasLocationPermission = hasLocationPermission,
        currentLocation = currentLocation,
        onRequestPermission = onRequestLocationPermission,
    )
    Spacer(modifier = Modifier.height(24.dp))
    NoticeComposerCard(appState = appState)
    Spacer(modifier = Modifier.height(20.dp))
    SectionTitle("Comunicados")
    Spacer(modifier = Modifier.height(14.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        appState.notices.forEach { notice ->
            NoticeCard(notice = notice)
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
    SectionTitle("Pagamentos para acompanhar")
    Spacer(modifier = Modifier.height(14.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        pendingPayments.forEach { (studentName, payment) ->
            PaymentOverviewCard(studentName = studentName, payment = payment)
        }
    }
}
