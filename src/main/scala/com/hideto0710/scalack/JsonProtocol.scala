package com.hideto0710.scalack

import com.hideto0710.scalack.models._
import spray.json.DefaultJsonProtocol

object JsonProtocol extends DefaultJsonProtocol {
  implicit val commentFmt = jsonFormat5(Comment)
  implicit val historyChunkFmt = jsonFormat4(HistoryChunk)
  implicit val channelValueFmt = jsonFormat3(ChannelValue)
  implicit val channelFmt = jsonFormat12(Channel)
  implicit val channelChunkFmt = jsonFormat3(ChannelChunk)
}