package bookingtour.protocols.core.actors.kafka

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class StrategyItem extends Product with Serializable

object StrategyItem {
  final case object WithEnvelope extends StrategyItem
  final case object OnlyBody     extends StrategyItem
}
