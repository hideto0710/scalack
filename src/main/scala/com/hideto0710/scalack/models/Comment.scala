package com.hideto0710.scalack.models

/**
 * コメントの情報
 * @param subtype サブタイプ
 * @param user ユーザー
 * @param text コメント
 * @param ts 日時（UNIX時刻）
 */
case class Comment(
  subtype: Option[String],
  user: Option[String],
  username: Option[String],
  text: String,
  ts: String
)