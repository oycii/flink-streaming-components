package com.github.oycii.env.monitoring.service

import com.github.oycii.common.dto.EnvMonitor
import com.github.oycii.env.monitoring.consts.Alarms
import com.github.oycii.env.monitoring.dto.EnvMonitorAlarm
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration

class AlarmMapService extends RichMapFunction[EnvMonitor, EnvMonitorAlarm] {
  private var envMonitorState: ValueState[EnvMonitor] = _

  override def map(newEnvMonitor: EnvMonitor): EnvMonitorAlarm = {
    val currentEnvMonitor = if (null == envMonitorState.value()) None else Some(envMonitorState.value())
    envMonitorState.update(newEnvMonitor)
    val alarm = currentEnvMonitor match {
      case Some(current) =>
        if ((current.co2 - newEnvMonitor.co2) > 0.5d) {
          Alarms.MAN_MADE_DISASTERS
        } else if ((current.speedOfWind - newEnvMonitor.speedOfWind) > 10) {
          Alarms.NATURAL_DISASTER
        } else {
          Alarms.NOT_CHANGED
        }
      case None => Alarms.NOT_CHANGED
    }

    EnvMonitorAlarm(newEnvMonitor.region, alarm)
  }

  override def open(config: Configuration): Unit = {
    val envMonitorStateDescriptor: ValueStateDescriptor[EnvMonitor] =
      new ValueStateDescriptor("envMonitorState", TypeInformation.of(classOf[EnvMonitor]))
    envMonitorState = getRuntimeContext.getState(envMonitorStateDescriptor)
  }
}
