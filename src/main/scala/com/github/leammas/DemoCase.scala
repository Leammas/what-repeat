package com.github.leammas

trait DemoCase[F[_]] {

  def demonstrate: F[Unit]

}
