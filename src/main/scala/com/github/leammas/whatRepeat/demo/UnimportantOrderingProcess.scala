package com.github.leammas.commutativity

import cats.data.NonEmptyList
import cats.effect.Sync
import cats.implicits._
import cats.effect.concurrent.Ref
import com.github.leammas.whatRepeat.DemoCase

object UnimportantOrderingProcess {

  def run[F[_]](repo: OrderRepo[F]): OrderEvent => F[Unit] =
    e => repo.saveOrderInfoFromEvent(e)

  def demo[F[_]](implicit F: Sync[F]): DemoCase[F] = new DemoCase[F] {
    def demonstrate: F[Unit] =
      for {
        _ <- F.delay(println("UnimportantOrderingProcess"))
        orderRef1 <- Ref.of[F, Map[String, OrderDBEntry]](
          Map.empty)
        orderRef2 <- Ref.of[F, Map[String, OrderDBEntry]](
          Map.empty)
        orderRepo1 = new InMemoryOrderRepo[F](orderRef1)
        orderRepo2 = new InMemoryOrderRepo[F](orderRef2)
        e1 = OrderCreated("1", NonEmptyList.of(OrderItem("foo")))
        e2 = OrderPaid("1")
        result1 <- run(orderRepo1)(e1) >> run(orderRepo1)(e2) >> orderRepo1.get(
          "1")
        result2 <- run(orderRepo2)(e2) >> run(orderRepo2)(e1) >> orderRepo2.get(
          "1")
        _ <- F.delay(println(result1))
        _ <- F.delay(println(result2))
      } yield ()
  }

}
