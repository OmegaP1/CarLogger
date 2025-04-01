package com.yourname.carlogger.ui.car.list

import com.yourname.carlogger.data.model.Car
import com.yourname.carlogger.data.model.CarImage

data class CarWithImage(
    val car: Car,
    val primaryImage: CarImage?
)