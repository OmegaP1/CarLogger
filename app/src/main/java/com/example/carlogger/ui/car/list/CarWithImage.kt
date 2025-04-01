package com.example.carlogger.ui.car.list

import com.example.carlogger.data.model.Car
import com.example.carlogger.data.model.CarImage

data class CarWithImage(
    val car: Car,
    val primaryImage: CarImage?
)