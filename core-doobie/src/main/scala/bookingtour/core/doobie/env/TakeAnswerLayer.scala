package bookingtour.core.doobie.env

import bookingtour.protocols.core.db.DbAnswer
import zio.Task

/**
  * © Alexey Toroshchin 2020.
  */
object TakeAnswerLayer {
  trait Service[A] {
    def ask: Task[DbAnswer.Payload[A]]
  }
}
