package com.github.leammas.commutativity

import cats.data.NonEmptyList
import cats.effect.Sync
import cats.effect.concurrent.Ref
import com.github.leammas.DemoCase
import cats.implicits._

object ByDesignSequentialProcess {

  def run[F[_]](repo: PaidItemCounter[F]): OrderEvent => F[CountResult] =
    e => repo.incrementForEvent(e)

  def demo[F[_]](implicit F: Sync[F]): DemoCase[F] = new DemoCase[F] {
    def demonstrate: F[Unit] =
      for {
        _ <- F.delay(println("ByDesignSequentialProcess"))
        orderItemCountRef <- Ref.of[F, Map[String, Int]](Map.empty)
        totalRef <- Ref.of[F, Int](0)
        itemCounter = new InMemoryItemCounter[F](orderItemCountRef, totalRef)
        e1 = OrderCreated("1", NonEmptyList.of(OrderItem("foo")))
        e2 = OrderPaid("1")
        result1 <- run(itemCounter)(e1) >> run(itemCounter)(e2) >> itemCounter.getCount
        result2 <- run(itemCounter)(e2) >> run(itemCounter)(e1) >> itemCounter.getCount
        _ <- F.delay(println(result1))
        _ <- F.delay(println(result2))
      } yield ()
  }

}
