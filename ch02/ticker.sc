import $exec.^.catsimp

import cats.syntax.flatMap._
import cats.Monad

import scala.concurrent.duration._

import debug._

object TickingClock extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    //tickingClock
    //whileMTick
    //foreverMTick
    byNameFlatMap
      .guaranteeCase {
        case ExitCase.Canceled => IO("canceled").debug().void
        case ExitCase.Completed => IO("completed").debug().void
        case ExitCase.Error(t) => IO(s"error: $t").debug().void
      }
      .as(ExitCode.Success)

  val tickingClock: IO[Unit] =
    for {
      t <- IO(System.currentTimeMillis())
      _ <- IO(s"time : $t").debug()
      _ <-IO.sleep(1.second)
      _ <- tickingClock
    } yield ()

  val singleTicker: IO[Unit] =
    for {
      t <- IO(System.currentTimeMillis())
      _ <- IO(s"time : $t").debug()
      _ <-IO.sleep(1.second)
    } yield ()

  val whileMTick: IO[Unit] = Monad[IO].whileM_(IO(true)){ singleTicker }

  val foreverMTick: IO[Unit] = singleTicker.foreverM

  // by name flatmap op
  val byNameFlatMap: IO[Unit] = singleTicker >> byNameFlatMap

}

TickingClock.run(List.empty[String]).unsafeRunSync()
