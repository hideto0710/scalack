package com.hideto0710.scalack.models

/**
 * SlackAPIのレスポンス
 */
sealed trait Response {
  val ok: Boolean
  val error: Option[String]
}

/**
 * channels.historyのレスポンス
 * @param ok リクエストの成功
 * @param error エラー内容
 * @param messages コメント一覧
 * @param has_more 次ページングの有無
 */
case class HistoryChunk(
  ok: Boolean, error: Option[String],
  messages: Option[Seq[Comment]],
  has_more: Option[Boolean]
) extends Response

/**
 * channel.listのレスポンス
 * @param ok リクエストの成功
 * @param error エラー内容
 * @param channels チャネルリスト
 */
case class ChannelChunk(
  ok: Boolean,
  error: Option[String],
  channels: Option[Seq[Channel]]
) extends Response