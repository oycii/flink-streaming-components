package com.github.oycii.tests.gen

import com.github.oycii.common.dto.EnvMonitor

object Generator {

  def getEnvMonitor = {
    EnvMonitor(
      region = scala.util.Random.nextInt(100),
      co2 = scala.util.Random.nextDouble(),
      h2oTemperature = scala.util.Random.nextInt(50),
      temperature = scala.util.Random.nextInt(100),
      airOfHumidity = scala.util.Random.nextDouble(),
      speedOfWind = scala.util.Random.nextInt(100),
      windDirection = scala.util.Random.nextInt(360),
      date = System.currentTimeMillis()
    )
  }

}
