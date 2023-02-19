package com.github.oycii.common.mapper

object NextMapper {
  implicit class Next[T](val obj: T) {
    def next[O](func: T => O) : O = func(obj)
  }
}
