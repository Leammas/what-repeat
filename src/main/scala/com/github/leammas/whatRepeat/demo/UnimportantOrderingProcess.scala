package com.github.leammas.whatRepeat.demo

import cats.effect.Sync
import cats.implicits._
import cats.effect.concurrent.Ref
import com.github.leammas.whatRepeat.DemoCase
import com.github.leammas.whatRepeat.demo.Common._

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
        run1 = run(orderRepo1)
        run2 = run(orderRepo2)
        result1 <- stream1[F].evalMap(run1).compile.drain >> orderRepo1.get(
          "1")
        result2 <- stream2[F].evalMap(run2).compile.drain >> orderRepo2.get(
          "1")
        _ <- F.delay(println(result1))
        _ <- F.delay(println(result2))
      } yield ()
  }

}
