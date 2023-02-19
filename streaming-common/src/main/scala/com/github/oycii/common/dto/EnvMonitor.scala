package com.github.oycii.common.dto

import java.util.Date

case class EnvMonitor(region: Long, co2: Double, h2oTemperature: Double, temperature: Double, airOfHumidity: Double, speedOfWind: Double, windDirection: Int, date: Long)
