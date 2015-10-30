package com.hideto0710.scalack.builders

import com.hideto0710.scalack.Scalack
import com.hideto0710.scalack.Scalack.Auth
import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ListChannelsSpec extends FlatSpec() with Matchers {

  val conf = ConfigFactory.load()
  implicit val auth = Auth(conf.getString("develop.token"))

  "An SlackApiClient" should "be able to get excludeArchived" in {
    val s = Scalack.listChannels.excludeArchived(1)
    s._excludeArchived.getOrElse(0) should be (1)
  }

  it should "be not able to get channel list because of toke" in {
    val f = Scalack.listChannels.excludeArchived(0).execute(Auth("v"))
    val result = Await.result(f, Duration.Inf)
    result.ok should be (right = false)
    result.error.get should be ("invalid_auth")
  }
}
