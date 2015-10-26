package com.hideto0710.scalack

import scala.annotation.tailrec
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

import akka.actor.ActorSystem

import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.client.pipelining._

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import com.hideto0710.scalack.models._

import JsonProtocol._

object Scalack {
  case class Auth(token: String)

  private type Pipeline[A] = (HttpRequest) => Future[A]
  private val ApiUrl = "https://slack.com/api/"
  private val logger = Logger(LoggerFactory.getLogger("Client"))
  private val Sleep = 5 * 1000

  implicit val system = ActorSystem()
  import system.dispatcher

  /**
   * GETリクエストのFutureを返す。
   * @param p Pipeline
   * @param uri リクエストURI
   * @tparam A レスポンス型
   * @return Future[A]
   * @note 型パラメータでは unmarshal ができないため、引数として受け取る。（implicit value 関連）
   */
  private def get[A](p: Pipeline[A], uri: Uri): Future[A] = {
    logger.debug(uri.toString())
    p(Get(uri))
  }

  /**
   * 同期リクエストのレスポンスを返す。
   * @param getFuture Futureの名前渡し引数
   * @param limit リクエスト試行回数制限
   * @param sleepTime リトライ時のThread.sleep（ms）
   * @param n リクエスト試行回数
   * @tparam A Futureの型
   * @return 同期リクエストのレスポンス
   */
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

  /**
   * Noneを排除し、valueをStringに変換したMapを返す。
   * @param argMap Noneを含んだMap
   * @return Noneが排除されたMap
   * @todo Someでラップしなくても良い方法を検討する。
   */
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
  def syncRequest[A<:Response](getFuture: => Future[A], limit: Int, sleepTime: Int=Sleep): Either[String, A] = {
    val result = tryHttpAwait(getFuture, limit, sleepTime)
    result match {
      case Right(r) =>
        if (r.ok) Right(r) else Left(r.error.getOrElse("unknown_error"))
      case Left(e) => Left(e.getMessage)
    }
  }

  def systemShutdown() = system.shutdown()

  private def makeUri(resource: String, queryParams: (String, Any)*): Uri = {
    val resourceUri = Uri(ApiUrl + resource)
    resourceUri withQuery Scalack.cleanMap(queryParams.toMap)
  }

  /**
   * channels.listのFutureを返す。
   * @param excludeArchived アーカイブチャネルの排除フラグ
   * @return channel.listのFuture
   */
  def listChannels(excludeArchived: Int = 0)(implicit a:Auth): Future[ChannelChunk] = {
    val requestUri = makeUri("channels.list", "token" -> a.token, "exclude_archived" -> excludeArchived)
    Scalack.get[ChannelChunk](
      sendReceive ~> unmarshal[ChannelChunk],
      requestUri
    )
  }

  /**
   * channels.historyのFutureを返す。
   * @param channel チャネル
   * @param latest 取得対象期間の終了日時
   * @param oldest 取得対象期間の開始日時
   * @param inclusive 指定期間のメッセージを含むフラグ
   * @param count メッセージ取得件数（最大1000）
   * @return channels.historyのFuture
   */
  def channelsHistory(
    channel: String,
    latest: Option[Long] = None,
    oldest: Option[Long] = None,
    inclusive: Option[Int] = None,
    count: Option[Int] = None
  )(implicit a:Auth): Future[HistoryChunk] = {
    val requestUri = makeUri(
      "channels.history", "token" -> a.token,
      "channel" -> channel, "latest" -> latest, "oldest" -> oldest, "inclusive" -> inclusive, "count" -> count
    )
    Scalack.get[HistoryChunk](
      sendReceive ~> unmarshal[HistoryChunk],
      requestUri
    )
  }
}