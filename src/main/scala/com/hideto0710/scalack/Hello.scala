package com.hideto0710.scalack

import com.hideto0710.scalack.Scalack.Auth
import com.hideto0710.scalack.builders.{ListChannels, ChannelsHistory}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

object Hello {

  val logger = Logger(LoggerFactory.getLogger("Hello"))
  val conf = ConfigFactory.load()

  implicit val auth = Auth(conf.getString("develop.token"))

  def main(args: Array[String]) = {
    val channelList = ListChannels.builder.excludeArchived(0).syncExecute match {
      case Right(r) => Some(r.channels.getOrElse(Seq.empty))
      case Left(e) => // MARK: channel.list取得エラー
        logger.error(s"SlackApiClient [$e] ERROR")
        None
    }

    if (channelList.getOrElse(Seq.empty).nonEmpty) {
      val headChannel = channelList.get.head
      val channelHistory = ChannelsHistory.builder.channel(headChannel.id).count(10).syncExecute match {
        case Right(r) => Some(r.messages.getOrElse(Seq.empty))
        case Left(e) => // MARK: channel.history取得エラー
          logger.error(s"SlackApiClient [$e] ERROR")
          None
      }
      if (channelHistory.getOrElse(Seq.empty).nonEmpty) {
        logger.debug(channelHistory.get.head.text)
      }
    }
    Scalack.systemShutdown()
  }
}