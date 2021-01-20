import $exec.^.catsimp

import scala.concurrent.duration._

import debug._

object TickingClock extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    tickingClock
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

}

TickingClock.run(List.empty[String]).unsafeRunSync()