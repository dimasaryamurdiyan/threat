package com.singaludra.threat.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.singaludra.threat.presentation.ui.screens.AlertDetailScreen
import com.singaludra.threat.presentation.ui.screens.AlertListScreen

object ThreatDestinations {
    const val ALERT_LIST_ROUTE = "alert_list"
    const val ALERT_DETAIL_ROUTE = "alert_detail"
    const val ALERT_ID_KEY = "alertId"
}

@Composable
fun ThreatNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = ThreatDestinations.ALERT_LIST_ROUTE
    ) {
        composable(ThreatDestinations.ALERT_LIST_ROUTE) {
            AlertListScreen(
                onNavigateToDetail = { alertId ->
                    navController.navigate("${ThreatDestinations.ALERT_DETAIL_ROUTE}/$alertId")
                }
            )
        }

        composable("${ThreatDestinations.ALERT_DETAIL_ROUTE}/{${ThreatDestinations.ALERT_ID_KEY}}") { backStackEntry ->
            val alertId = backStackEntry.arguments?.getString(ThreatDestinations.ALERT_ID_KEY) ?: ""
            AlertDetailScreen(
                alertId = alertId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}