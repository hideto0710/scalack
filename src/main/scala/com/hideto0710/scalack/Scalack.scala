package com.hideto0710.scalack

import scala.annotation.tailrec
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

import akka.actor.ActorSystem
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.client.pipelining._

import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger

import com.hideto0710.scalack.models._

import JsonProtocol._

object Scalack {
  case class Auth(token: String)

  private type Pipeline[A] = (HttpRequest) => Future[A]
  private val ApiUrl = "https://slack.com/api/"
  private val logger = Logger(LoggerFactory.getLogger("Scalack"))
  private val Sleep = 5 * 1000

  implicit val system = ActorSystem()
  import system.dispatcher

  private def get[A](p: Pipeline[A], uri: Uri): Future[A] = {
    logger.debug(uri.toString())
    p(Get(uri))
  }

  @tailrec
  private def tryHttpAwait[A](getFuture: => Future[A], limit: Int, sleepTime: Int, n: Int=1): Either[Throwable, A] = {
    if (n > 1) {
      logger.debug(s"tryHttpAwait sleep $sleepTime ms")
      Thread.sleep(sleepTime)
    }
    val f = getFuture
    Await.ready(f, Duration.Inf)
    f.value.get match {
      case Success(r) => Right(r)
      case Failure(e) =>
        if (n < limit) tryHttpAwait(getFuture, limit, sleepTime, n+1) else Left(e)
    }
  }

  private def cleanMap(argMap: Map[String, Any]): Map[String, String] = {
    argMap.map {
      case (k, None) => (k, None)
      case (k, Some(v)) => (k, Some(v.toString))
      case (k, v) => (k, Some(v.toString))
    }.collect {
      case (k, Some(v)) => (k, v)
    }
  }

  /**
   * 同期リクエストのレスポンスを返す。
   * @param getFuture Future（非同期リクエスト）
   * @param limit リクエスト試行回数制限
   * @param sleepTime リトライ時のThread.sleep（ms）
   * @tparam A Futureの型
   * @return 同期リクエストのレスポンス
   */
  def syncRequest[A<:Response](getFuture: => Future[A], limit: Int=0, sleepTime: Int=Sleep): Either[String, A] = {
    val result = tryHttpAwait(getFuture, limit, sleepTime)
    result match {
      case Right(r) =>
        if (r.ok) Right(r) else Left(r.error.getOrElse("unknown_error"))
      case Left(e) => Left(e.getMessage)
    }
  }

  def makeUri(resource: String, queryParams: (String, Any)*): Uri = {
    val resourceUri = Uri(ApiUrl + resource)
    resourceUri withQuery cleanMap(queryParams.toMap)
  }

  def systemShutdown() = system.shutdown()

  def listChannels(uri: Uri) = get[ChannelChunk](sendReceive ~> unmarshal[ChannelChunk], uri)
  def channelsHistory(uri: Uri) = get[HistoryChunk](sendReceive ~> unmarshal[HistoryChunk], uri)

}