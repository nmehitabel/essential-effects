import $exec.^.catsimp

import debug._

import cats.implicits._
import scala.concurrent.duration._

object ResourceBackgroudTask extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- backgroudTask.use { _ =>
            IO.sleep(700.millis) *> IO("Sleep is cool").debug
          }
      _ <- IO("Done").debug
    } yield ExitCode.Success

  val backgroudTask: Resource[IO, Unit] = {
    val loop = (IO("loopage...").debug *> IO.sleep(100.millis)).foreverM

    Resource.make(IO(">>> Forking backgroud task").debug *> loop.start)(
      IO("<<< Cancelling background task").debug.void *> _.cancel
    )
    .void
  }

}

ResourceBackgroudTask.run(List.empty[String]).unsafeRunSync