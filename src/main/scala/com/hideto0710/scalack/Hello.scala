package com.hideto0710.scalack

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

object Hello {

  val logger = Logger(LoggerFactory.getLogger("Hello"))

  def main(args: Array[String]) = {
    logger.debug("hello")
  }
}