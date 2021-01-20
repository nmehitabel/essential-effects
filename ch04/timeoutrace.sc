import $exec.^.catsimp

import cats.effect.implicits._
import scala.concurrent.duration._

import debug._

object Timeout extends IOApp{
  def run(args: List[String]): IO[ExitCode] =
    for {
      done <- IO.race(task, timeout)
      _ <- done match {
        case Left(_)  => IO("   Task: won").debug
        case Right(_) => IO("Timeout: won").debug
      }
    } yield ExitCode.Success

  val task: IO[Unit]   = annotatedSleep("   Task", 1000.millis)
  val timeout: IO[Unit] = annotatedSleep("Timeout", 500.millis)

  def annotatedSleep(name: String, duration: FiniteDuration): IO[Unit] =
    (
      IO(s"$name : Starting").debug *>
      IO.sleep(duration) *>
      IO(s"$name : Done").debug
    ).onCancel(IO(s"$name : Cancelled").debug.void).void

}

Timeout.run(List.empty[String]).unsafeRunSync