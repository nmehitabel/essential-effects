import $exec.^.catsimp

import debug._

object Blocking extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    Blocker[IO].use { blocker =>
      withBlocker(blocker).as(ExitCode.Success)
    }

  def withBlocker(blocker: Blocker): IO[Unit] =
    for {
      _ <- IO("on Default EC").debug
      _ <- blocker.blockOn(IO("on Blocker EC").debug)
      _ <- IO("Where Am I Now?").debug
    } yield ()

}

Blocking.run(List.empty[String]).unsafeRunSync