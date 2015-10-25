package com.hideto0710.scalack

import com.hideto0710.scalack.Scalack.Auth
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

object Hello {

  val logger = Logger(LoggerFactory.getLogger("Hello"))
  val conf = ConfigFactory.load()

  implicit val auth = Auth("<Your Token>")

  def main(args: Array[String]) = {
    val channelResult = Scalack.syncRequest(Scalack.listChannels(1), 0)
    val channelList = channelResult match {
      case Right(r) => Some(r.channels.getOrElse(Seq.empty))
      case Left(e) => // MARK: channel.list取得エラー
        logger.error(s"SlackApiClient [$e] ERROR")
        None
    }
    if (channelList.getOrElse(Seq.empty).nonEmpty) {
      logger.debug(channelList.get.head.toString)
    }
    Scalack.systemShutdown()
  }
}