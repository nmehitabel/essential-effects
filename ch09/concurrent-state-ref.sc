import $exec.^.catsimp

import cats.effect.concurrent.Ref
import cats.implicits._
import scala.concurrent.duration._
import debug._

object ConcurrentStateRef extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    for {
      ticks <- Ref[IO].of(0L)
      _ <- (tickingClock(ticks), printTicks(ticks)).parTupled
    } yield ExitCode.Success


  def tickingClock(ticks: Ref[IO, Long]): IO[Unit] =
    for {
      _ <- IO.sleep(1.second)
      _ <- IO(System.currentTimeMillis).debug
      _ <-  ticks.update(_ + 1)
      _ <- tickingClock(ticks)
    } yield ()

  def printTicks(ticks: Ref[IO, Long]): IO[Unit] =
    for {
      _ <- IO.sleep(5.seconds)
      tick <- ticks.get
      _ <- IO(s"TICKS: $tick").debug.void
      _ <- printTicks(ticks)
    } yield ()
}

ConcurrentStateRef.run(List.empty[String]).unsafeRunSync
