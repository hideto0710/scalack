package com.hideto0710.scalack.builders

sealed trait TBoolean
sealed trait TTrue extends TBoolean
sealed trait TFalse extends TBoolean