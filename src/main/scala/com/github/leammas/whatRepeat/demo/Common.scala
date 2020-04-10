package com.github.leammas.whatRepeat.demo

import cats.data.NonEmptyList
import fs2.Stream

object Common {
  val e1 = OrderCreated("1", NonEmptyList.of(OrderItem("foo")))
  val e2 = OrderPaid("1")
  def stream1[F[_]] = Stream.emits[F, OrderEvent](List(e1, e2))
  def stream2[F[_]] = Stream.emits[F, OrderEvent](List(e2, e1))
}
