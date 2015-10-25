package com.hideto0710.scalack.models

/**
 * channelの情報
 * @param id ID
 * @param name 名前
 * @param created 作成日時（UNIX時刻）
 * @param creator 作成者
 * @param members 参加メンバー
 * @param num_members 参加メンバー数
 * @param is_channel チャネルであるか
 * @param is_archived アーカイブされているか
 * @param is_general generalであるか
 * @param is_member 自身がメンバーであるか
 * @param topic トピック
 * @param purpose 目的
 */
case class Channel(
  id: String,
  name: String,
  created: Long,
  creator: String,
  members: Seq[String],
  num_members: Int,
  is_channel: Boolean,
  is_archived:Boolean,
  is_general:Boolean,
  is_member: Boolean,
  topic: ChannelValue,
  purpose: ChannelValue
)