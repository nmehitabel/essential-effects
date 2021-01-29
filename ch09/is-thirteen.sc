import $exec.^.catsimp

import cats.effect.concurrent._
import cats.implicits._
import scala.concurrent.duration._
import debug._

object IsThirteen extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    for {
      ticks <- Ref[IO].of(0L)
      is13 <- Deferred[IO, Unit]
      _ <- (beepWhen(is13), tickingClock(ticks, is13)).parTupled
    } yield ExitCode.Success

  def beepWhen(is13: Deferred[IO, Unit]) =
    for {
      _ <- is13.get
      _ <- IO("BEEP!").debug
    } yield ()

  def tickingClock(ticks: Ref[IO, Long], is13: Deferred[IO, Unit]): IO[Unit] =
    for {
      _ <- IO.sleep(1.second)
      _ <- IO(System.currentTimeMillis).debug
      count <- ticks.updateAndGet(_ + 1)
      _ <- if (count == 13) is13.complete(()) else IO.unit
      _ <- tickingClock(ticks, is13)
    } yield ()
}

IsThirteen.run(List.empty[String]).unsafeRunSync
