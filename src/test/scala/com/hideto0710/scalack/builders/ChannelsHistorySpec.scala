package com.hideto0710.scalack.builders

import com.hideto0710.scalack.Scalack
import com.hideto0710.scalack.Scalack.Auth
import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ChannelsHistorySpec extends FlatSpec() with Matchers {

  val conf = ConfigFactory.load()
  implicit val auth = Auth(conf.getString("develop.token"))

  val channelList = Scalack.listChannels.excludeArchived(1).syncExecute match {
    case Right(r) => Some(r.channels.getOrElse(Seq.empty))
    case Left(e) => None
  }
  val channel = channelList.get.head.id

  "An ChannelsHistory" should "be able to get channel history" in {
    val f = Scalack.channelsHistory.channel(channel).count(1).execute
    val result = Await.result(f, Duration.Inf)
    result.ok should be (right = true)
    result.messages.toArray should not be empty
  }

  it should "be able to get channel history sync" in {
    Scalack.channelsHistory.channel(channel).count(1).syncExecute match {
      case Right(r) =>
        r.ok should be (right = true)
        r.messages.get should not be empty
      case Left(e) => println(e)
    }
  }

  it should "be not able to get channel history because of toke" in {
    val f = Scalack.channelsHistory.channel(channel).count(1).execute(Auth("v"))
    val result = Await.result(f, Duration.Inf)
    result.ok should be (right = false)
    result.error.get should be ("invalid_auth")
  }

  // MARK: To check builder is immutable
  val builder = Scalack.channelsHistory.channel(channel)
  it should "be able to get 2 messages" in {
    val f = builder.count(2).execute
    val result = Await.result(f, Duration.Inf)
    result.ok should be (right = true)
    result.messages.get.length should equal(2)
  }

  it should "be able to get 3 messages" in {
    val f = builder.count(3).execute
    val result = Await.result(f, Duration.Inf)
    result.ok should be (right = true)
    result.messages.get.length should equal(3)
  }
}
