package com.github.leammas.whatRepeat.demo

import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.implicits._
import com.github.leammas.whatRepeat.DemoCase
import com.github.leammas.whatRepeat.demo.Common._

object ByDesignSequentialProcess {

  def run[F[_]: MonadThrowable](
      repo: PaidItemCounter[F]): OrderEvent => F[Unit] =
    e =>
      repo.incrementForEvent(e).flatMap {
        case CountResult.ItemCountSaved => ().pure[F]
        case CountResult.OrderNotFound =>
          (new RuntimeException("OrderNotFound")).raiseError[F, Unit]
    }

  def demo[F[_]](implicit F: Sync[F]): DemoCase[F] = new DemoCase[F] {
    def demonstrate: F[Unit] =
      for {
        _ <- F.delay(println("ByDesignSequentialProcess"))
        orderItemCountRef1 <- Ref.of[F, Map[String, Int]](Map.empty)
        totalRef1 <- Ref.of[F, Int](0)
        orderItemCountRef2 <- Ref.of[F, Map[String, Int]](Map.empty)
        totalRef2 <- Ref.of[F, Int](0)
        itemCounter1 = new InMemoryItemCounter[F](orderItemCountRef1, totalRef1)
        itemCounter2 = new InMemoryItemCounter[F](orderItemCountRef2, totalRef2)
        run1 = run(itemCounter1)
        run2 = run(itemCounter2)
        result1 <- stream1[F].evalMap(run1).compile.drain >> itemCounter1.getCount
        result2 <- stream2[F]
          .evalMap(run2)
          .compile
          .drain.handleError(_ => ()) >> itemCounter2.getCount
        _ <- F.delay(println(result1))
        _ <- F.delay(println(result2))
      } yield ()
  }

}
