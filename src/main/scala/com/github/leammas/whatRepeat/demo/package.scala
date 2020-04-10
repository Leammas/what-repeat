package com.github.leammas.whatRepeat
import cats.{Functor, Monad}
import cats.data.NonEmptyList
import cats.effect.concurrent.Ref
import cats.implicits._

package object demo {

  final case class OrderItem(name: String)

  sealed trait OrderEvent extends Product with Serializable {
    def id: String
  }
  final case class OrderCreated(id: String, items: NonEmptyList[OrderItem])
      extends OrderEvent
  final case class OrderPaid(id: String) extends OrderEvent

  final case class OrderDBEntry(id: String,
                                items: Option[NonEmptyList[OrderItem]],
                                isPaid: Boolean)

  object OrderDBEntry {
    def fromEvent(e: OrderEvent)(
        prevState: Option[OrderDBEntry]): OrderDBEntry =
      e match {
        case OrderCreated(id, items) =>
          prevState.fold(OrderDBEntry(id, items.some, isPaid = false))(
            _.copy(items = items.some))
        case OrderPaid(id) =>
          prevState.fold(OrderDBEntry(id, None, isPaid = true))(
            _.copy(isPaid = true))
      }
  }

  trait OrderRepo[F[_]] {
    def saveOrderInfoFromEvent(e: OrderEvent): F[Unit]

    def get(id: String): F[Option[OrderDBEntry]]
  }

  final class InMemoryOrderRepo[F[_]: Functor](
      r: Ref[F, Map[String, OrderDBEntry]])
      extends OrderRepo[F] {

    def saveOrderInfoFromEvent(e: OrderEvent): F[Unit] = r.update { s =>
      s.updated(e.id, OrderDBEntry.fromEvent(e)(s.get(e.id)))
    }

    def get(id: String): F[Option[OrderDBEntry]] = r.get.map(_.get(id))
  }

  sealed trait CountResult extends Product with Serializable
  object CountResult {
    case object ItemCountSaved extends CountResult
    case object OrderNotFound extends CountResult

    def itemCountSaved: CountResult = ItemCountSaved
    def orderNotFound: CountResult = OrderNotFound
  }

  trait PaidItemCounter[F[_]] {
    def incrementForEvent(e: OrderEvent): F[CountResult]

    def getCount: F[Int]
  }

  final class InMemoryItemCounter[F[_]: Monad](r: Ref[F, Map[String, Int]],
                                               total: Ref[F, Int])
      extends PaidItemCounter[F] {
    def incrementForEvent(e: OrderEvent): F[CountResult] =
      e match {
        case OrderCreated(id, items) =>
          r.update { s =>
              s.updated(id, items.length)
            }
            .as(CountResult.itemCountSaved)
        case OrderPaid(id) =>
          r.get.map(_.get(id)).flatMap {
            _.fold(CountResult.orderNotFound.pure[F].widen)(
              c =>
                total
                  .update(total => total + c) >> r
                  .update(x => x.removed(id))
                  .as(CountResult.itemCountSaved)
            )
          }
      }

    def getCount: F[Int] = total.get
  }
}
