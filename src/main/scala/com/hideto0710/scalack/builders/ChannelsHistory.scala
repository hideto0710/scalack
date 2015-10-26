package com.hideto0710.scalack.builders

import com.hideto0710.scalack.Scalack
import com.hideto0710.scalack.Scalack.Auth

object ChannelsHistory {

  case class Builder(
    _channel: Option[String],
    _latest: Option[Long],
    _oldest: Option[Long],
    _inclusive: Option[Int],
    _count: Option[Int]
  ) {
    def channel(channel: String) = this.copy(_channel = Some(channel))
    def count(count: Int) = this.copy(_count = Some(count))

    def executeSync(implicit a:Auth) = {
      Scalack.syncRequest(Scalack.channelsHistory(_channel.get, _latest, _oldest, _inclusive, _count), 0)
    }
    def execute(implicit a:Auth) = {
      Scalack.channelsHistory(_channel.get, _latest, _oldest, _inclusive, _count)
    }
  }

  val builder = Builder(None, None, None, None, None)
}
