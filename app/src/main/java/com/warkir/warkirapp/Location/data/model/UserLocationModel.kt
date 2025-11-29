package com.warkir.warkirapp.Location.data.model

data class UserLocationModel(
    val isLocationSet: Boolean,
    val userAddress: String,
    val userLatitude: Double,
    val userLongitude: Double
)
