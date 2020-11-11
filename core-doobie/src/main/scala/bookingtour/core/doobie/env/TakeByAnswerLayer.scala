package bookingtour.core.doobie.env

import bookingtour.protocols.core.db.DbAnswer
import zio.Task

/**
  * © Alexey Toroshchin 2020.
  */
object TakeByAnswerLayer {
  trait Service[Id, Value] {
    def ask(id: List[Id]): Task[DbAnswer.Payload[Value]]
  }
}
