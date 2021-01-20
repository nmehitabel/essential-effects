import $exec.^.catsimp

import scala.concurrent.duration._

object TickingClock extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    tickingClock.as(ExitCode.Success)

  val tickingClock: IO[Unit] =
    for {
      t <- IO(System.currentTimeMillis())
      _ <- IO(println(s"time : $t"))
      _ <-IO.sleep(1.second)
      _ <- tickingClock
    } yield ()

}

TickingClock.run(List.empty[String]).unsafeRunSync()