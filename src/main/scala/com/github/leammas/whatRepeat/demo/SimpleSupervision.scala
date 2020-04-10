package com.github.leammas.whatRepeat.demo

import cats.implicits._

object SimpleSupervision {

  def runSupervised[F[_]: MonadThrowable](process: F[Unit]): F[Unit] = {
    def supervise(process: F[Unit]): F[Unit] =
      process.handleErrorWith { _ =>
        supervise(process)
      }

    supervise(process)
  }

}
