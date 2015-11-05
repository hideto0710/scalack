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

  "An ChannelsHistory" should "be able to get channel" in {
    val s = Scalack.channelsHistory.channel("general").count(1)
    s._channel.get should be ("general")
  }

  it should "be able to get count" in {
    val s = Scalack.channelsHistory.channel("general").count(1)
    s._count.get should be (1)
  }

  /*it should "be not able to get channel history because of toke" in {
    val f = Scalack.channelsHistory.channel("general").count(1).execute(Auth("v"))
    val result = Await.result(f, Duration.Inf)
    result.ok should be (right = false)
    result.error.get should be ("invalid_auth")
  }*/

  // MARK: To check builder is immutable
  val builder = Scalack.channelsHistory
  it should "be able to get 2 messages" in {
    val s = builder.count(2)
    s._count.get should be (2)
  }

  it should "be able to get 3 messages" in {
    val s = builder.count(3)
    s._count.get should be (3)
  }
}
