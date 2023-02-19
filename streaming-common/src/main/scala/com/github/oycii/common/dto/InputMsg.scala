package com.github.oycii.common.dto

case class InputMsg(key: Array[Byte], message: Array[Byte], meta: Map[String, String], partition: Int)
