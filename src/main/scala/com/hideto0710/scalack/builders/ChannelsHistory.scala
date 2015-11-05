package com.hideto0710.scalack.builders

import com.hideto0710.scalack.Scalack
import com.hideto0710.scalack.Scalack.Auth

sealed trait BuilderMethods {
  type ChannelCalled <: TBoolean
}

object ChannelsHistory {

  type UnusedBuilder = BuilderMethods {type ChannelCalled = TFalse}

  case class Builder[M <: BuilderMethods](
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

    def channel(channel: String): Builder[M {type ChannelCalled = TTrue}] =
      this.copy(_channel = Some(channel))

    def latest(latest: Long): Builder[M] = this.copy(_latest = Some(latest))
    def oldest(oldest: Long): Builder[M] = this.copy(_oldest = Some(oldest))
    def inclusive(inclusive: Int): Builder[M] = this.copy(_inclusive = Some(inclusive))
    def count(count: Int): Builder[M] = this.copy(_count = Some(count))

    def syncExecute(implicit a: Auth, t: M#ChannelCalled =:= TTrue) = Scalack.syncRequest(get)
    def execute(implicit a: Auth, t: M#ChannelCalled =:= TTrue) = get
  }

  val builder = Builder[UnusedBuilder](None, None, None, None, None)
}
