package com.github.leammas.whatRepeat

import cats.effect.{ExitCode, IO, IOApp}
import com.github.leammas.commutativity.{ByDesignSequentialProcess, UnimportantOrderingProcess}

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    List(UnimportantOrderingProcess.demo[IO], ByDesignSequentialProcess.demo[IO])
      .traverse(_.demonstrate)
      .as(ExitCode.Success)
}
