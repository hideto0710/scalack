package com.hideto0710.scalack.models

/**
 * channelの項目毎の詳細情報
 * @param value 項目に関する情報
 * @param creator 更新者
 * @param last_set 更新日時
 */
case class ChannelValue(
  value: String,
  creator: String,
  last_set: Long
)
