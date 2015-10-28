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

  "An SlackApiClient" should "be able to get channel list" in {
    val f = Scalack.listChannels.excludeArchived(1).execute
    val result = Await.result(f, Duration.Inf)
    result.ok should be (right = true)
    result.channels.toArray should not be empty
  }

  it should "be able to get channel list sync" in {
    Scalack.listChannels.excludeArchived(0).syncExecute match {
      case Right(r) =>
        r.ok should be (right = true)
        r.channels.get should not be empty
      case Left(e) => println(e)
    }
  }

  it should "be not able to get channel list because of toke" in {
    val f = Scalack.listChannels.excludeArchived(0).execute(Auth("v"))
    val result = Await.result(f, Duration.Inf)
    result.ok should be (right = false)
    result.error.get should be ("invalid_auth")
  }
}
