package bookingtour.core.actors.kafka.queries.client.query

/**
  * © Alexey Toroshchin 2020.
  */
final case class AddToQueue[A](cell: QueueCell[A])
