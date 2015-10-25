package com.hideto0710.scalack

import com.hideto0710.scalack.Client.Slack
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

object Hello {

  val logger = Logger(LoggerFactory.getLogger("Hello"))
  val conf = ConfigFactory.load()

  implicit val slack = Slack("<Your Token>")

  def main(args: Array[String]) = {
    val channelResult = Client.syncRequest(Client.listChannels(1), 0)
    val channelList = channelResult match {
      case Right(r) => Some(r.channels.getOrElse(Seq()))
      case Left(e) => // MARK: channel.list取得エラー
        logger.error(s"SlackApiClient [$e] ERROR")
        None
    }
    logger.debug(channelList.getOrElse(Seq()).head.toString)
    Client.systemShutdown()
  }
}