package com.github.leammas.whatRepeat

trait DemoCase[F[_]] {

  def demonstrate: F[Unit]

}
