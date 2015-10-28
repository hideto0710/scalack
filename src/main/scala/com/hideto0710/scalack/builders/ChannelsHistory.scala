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

    private def get(implicit a:Auth) = {
      Scalack._channelsHistory(Scalack.makeUri(
        "channels.history",
        "token" -> a.token,
        "channel" -> _channel,
        "latest" -> _latest,
        "oldest" -> _oldest,
        "inclusive" -> _inclusive,
        "count" -> _count
      ))
    }

    def channel(channel: String) = this.copy(_channel = Some(channel))
    def latest(latest: Long) = this.copy(_latest = Some(latest))
    def oldest(oldest: Long) = this.copy(_oldest = Some(oldest))
    def inclusive(inclusive: Int) = this.copy(_inclusive = Some(inclusive))
    def count(count: Int) = this.copy(_count = Some(count))

    def syncExecute(implicit a:Auth) = Scalack.syncRequest(get)
    def execute(implicit a:Auth) = get
  }

  val builder = Builder(None, None, None, None, None)
}
