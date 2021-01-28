import $exec.^.catsimp

import cats.implicits._
import scala.concurrent.duration._
import debug._

object ConcurrentStateVar extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- (tickingClock, printTicks).parTupled
    } yield ExitCode.Success

  var ticks: Long = 0L

  val tickingClock: IO[Unit] =
    for {
      _ <- IO.sleep(1.second)
      _ <- IO(System.currentTimeMillis).debug
      _ =  ticks = ticks + 1
      _ <- tickingClock
    } yield ()

  val printTicks: IO[Unit] =
    for {
      _ <- IO.sleep(5.seconds)
      _ <- IO(s"TICKS: $ticks").debug.void
      _ <- printTicks
    } yield ()
}

ConcurrentStateVar.run(List.empty[String]).unsafeRunSync
