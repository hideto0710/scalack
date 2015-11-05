package com.hideto0710.scalack.models

/**
 * channel
 * @param id ID
 * @param name the name of the channel.
 * @param created a unix timestamp.
 * @param creator the user ID of the member that created this channel.
 * @param members a list of user ids for all users in this channel.
 * @param num_members a number of members.
 * @param is_channel will be true if this is channel.
 * @param is_archived will be true if the channel is archived.
 * @param is_general will be true if this channel is the "general" channel that includes all regular team members.
 * @param is_member will be true if the calling member is part of the channel.
 * @param topic information about the channel topic.
 * @param purpose information about the channel purpose
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