import $exec.^.catsimp

import debug._

object Never extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    never
      .guarantee(IO("Now is Never").debug.void)
      .as(ExitCode.Success)

  val never: IO[Nothing] =
    // just never use the callback
    IO.async(_ => ())

}

Never.run(List.empty[String]).unsafeRunSync