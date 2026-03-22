package com.example.voyagetime.data.source

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Tour
import com.example.voyagetime.ui.screens.ItineraryDayData
import com.example.voyagetime.ui.screens.ItineraryEvent

object FakeItineraryDataSource {

    private val tripsData: MutableMap<String, MutableList<ItineraryDayData>> =
        mutableMapOf(
            "paris" to createParisTripData(),
            "tokyo" to createTokyoTripData(),
            "barcelona" to createBarcelonaTripData(),
            "newyork" to createNewYorkTripData()
        )

    fun getStoredTripDaysOrNull(tripId: String): MutableList<ItineraryDayData>? {
        return tripsData[tripId]
    }

    fun replaceTripDays(tripId: String, newDays: MutableList<ItineraryDayData>) {
        tripsData[tripId] = newDays
    }

    private fun createParisTripData(): MutableList<ItineraryDayData> {
        return mutableListOf(
            ItineraryDayData(
                dayLabel = "Day 1",
                dayDate = "13/06/2026",
                morningPlan = mutableListOf(
                    ItineraryEvent("08:00", "Breakfast at Café de Flore", "Saint-Germain-des-Prés", "€14", Icons.Default.Restaurant),
                    ItineraryEvent("10:00", "Visit Louvre Museum", "Rue de Rivoli", "€22", Icons.Default.Tour)
                ),
                afternoonPlan = mutableListOf(
                    ItineraryEvent("13:00", "Lunch near Tuileries", "1st arrondissement", "€18", Icons.Default.Restaurant),
                    ItineraryEvent("15:30", "Seine River Walk", "Pont Neuf", "Free", Icons.Default.Map)
                ),
                eveningPlan = mutableListOf(
                    ItineraryEvent("19:00", "Eiffel Tower Visit", "Champ de Mars", "€26", Icons.Default.Place)
                ),
                notes = "Buy museum ticket online before arrival."
            ),
            ItineraryDayData(
                dayLabel = "Day 2",
                dayDate = "14/06/2026",
                morningPlan = mutableListOf(
                    ItineraryEvent("09:00", "Coffee and croissant", "Le Marais", "€9", Icons.Default.Restaurant)
                ),
                afternoonPlan = mutableListOf(
                    ItineraryEvent("14:00", "Notre-Dame area visit", "Île de la Cité", "Free", Icons.Default.Place)
                ),
                eveningPlan = mutableListOf(
                    ItineraryEvent("19:30", "Dinner in Latin Quarter", "Latin Quarter", "€24", Icons.Default.Restaurant)
                ),
                notes = "Metro is the fastest option for moving between areas."
            )
        )
    }

    private fun createTokyoTripData(): MutableList<ItineraryDayData> {
        return mutableListOf(
            ItineraryDayData(
                dayLabel = "Day 1",
                dayDate = "03/08/2026",
                morningPlan = mutableListOf(
                    ItineraryEvent("08:30", "Breakfast in Shibuya", "Shibuya Station Area", "€12", Icons.Default.Restaurant),
                    ItineraryEvent("10:30", "Meiji Shrine Visit", "Shibuya", "Free", Icons.Default.Tour)
                ),
                afternoonPlan = mutableListOf(
                    ItineraryEvent("13:00", "Lunch in Harajuku", "Takeshita Street", "€18", Icons.Default.Restaurant),
                    ItineraryEvent("15:00", "Tokyo Skytree", "Sumida", "€24", Icons.Default.Place)
                ),
                eveningPlan = mutableListOf(
                    ItineraryEvent("19:30", "Dinner in Akihabara", "Akihabara", "€32", Icons.Default.Map)
                ),
                notes = "Use metro card. Start early to avoid queues."
            ),
            ItineraryDayData(
                dayLabel = "Day 2",
                dayDate = "04/08/2026",
                morningPlan = mutableListOf(
                    ItineraryEvent("09:00", "Ueno Park Walk", "Ueno", "Free", Icons.Default.Map)
                ),
                afternoonPlan = mutableListOf(
                    ItineraryEvent("13:00", "Tokyo National Museum", "Ueno", "€16", Icons.Default.Tour)
                ),
                eveningPlan = mutableListOf(
                    ItineraryEvent("19:00", "Dinner in Ginza", "Ginza", "€28", Icons.Default.Restaurant)
                ),
                notes = "Comfortable shoes recommended."
            )
        )
    }

    private fun createBarcelonaTripData(): MutableList<ItineraryDayData> {
        return mutableListOf(
            ItineraryDayData(
                dayLabel = "Day 1",
                dayDate = "11/03/2026",
                morningPlan = mutableListOf(
                    ItineraryEvent("09:00", "Breakfast near Plaça Catalunya", "City Center", "€9", Icons.Default.Restaurant),
                    ItineraryEvent("11:00", "Sagrada Família Visit", "Eixample", "€26", Icons.Default.Tour)
                ),
                afternoonPlan = mutableListOf(
                    ItineraryEvent("14:00", "Lunch in El Born", "El Born", "€19", Icons.Default.Restaurant)
                ),
                eveningPlan = mutableListOf(
                    ItineraryEvent("18:00", "Walk at Barceloneta", "Barceloneta", "Free", Icons.Default.Map)
                ),
                notes = "Metro ticket useful for all day."
            )
        )
    }

    private fun createNewYorkTripData(): MutableList<ItineraryDayData> {
        return mutableListOf(
            ItineraryDayData(
                dayLabel = "Day 1",
                dayDate = "05/12/2025",
                morningPlan = mutableListOf(
                    ItineraryEvent("08:30", "Breakfast near Bryant Park", "Midtown", "€16", Icons.Default.Restaurant),
                    ItineraryEvent("10:30", "Top of the Rock", "Rockefeller Center", "€38", Icons.Default.Place)
                ),
                afternoonPlan = mutableListOf(
                    ItineraryEvent("13:30", "Lunch in Chelsea", "Chelsea Market", "€24", Icons.Default.Restaurant),
                    ItineraryEvent("15:30", "High Line Walk", "Manhattan West Side", "Free", Icons.Default.Map)
                ),
                eveningPlan = mutableListOf(
                    ItineraryEvent("19:30", "Times Square at night", "Times Square", "Free", Icons.Default.Tour)
                ),
                notes = "Cold weather trip. Keep gloves and power bank ready."
            )
        )
    }
}