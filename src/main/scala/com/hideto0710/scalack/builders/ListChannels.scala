package com.hideto0710.scalack.builders

import com.hideto0710.scalack.Scalack
import com.hideto0710.scalack.Scalack.Auth

object ListChannels {

  case class Builder(
    _excludeArchived: Option[Int]
  ) {

    private def get(implicit a:Auth) = {
      Scalack.listChannels(Scalack.makeUri(
        "channels.list",
        "token" -> a.token,
        "exclude_archived" -> _excludeArchived
      ))
    }

    def excludeArchived(excludeArchived: Int) = this.copy(_excludeArchived = Some(excludeArchived))

    def syncExecute(implicit a:Auth) = Scalack.syncRequest(get)
    def execute(implicit a:Auth) = get
  }

  val builder = Builder(None)
}
