package com.hideto0710.scalack.models

/**
 * channel information
 * @param value detail of this information.
 * @param creator the user ID of the member that update this information.
 * @param last_set a unix timestamp.
 */
case class ChannelValue(
  value: String,
  creator: String,
  last_set: Long
)
