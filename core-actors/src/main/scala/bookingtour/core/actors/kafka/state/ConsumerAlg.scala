package bookingtour.core.actors.kafka.state

import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
trait ConsumerAlg extends Serializable {
  val consumerAlg: ConsumerAlg.Service[Any]
}

object ConsumerAlg {
  final type Aux[R, PartitionKey, Value] = Service[R] {
    type Key    = PartitionKey
    type Result = Value
  }

  trait Service[R] {
    type Key
    type Result

    def all(): ZIO[R, String, List[Result]]

    def byKey(condition: Key => Boolean): ZIO[R, String, List[Result]]

    def byValue(condition: Result => Boolean): ZIO[R, String, List[Result]]

    def byKeyValue(
        conditionKey: Key => Boolean,
        conditionValue: Result => Boolean
    ): ZIO[R, String, List[Result]]
  }
}
