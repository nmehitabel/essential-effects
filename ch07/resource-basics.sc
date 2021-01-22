import $exec.^.catsimp

import debug._

object BasicResource extends IOApp{
  def run(args: List[String]): IO[ExitCode] =
    stringResource
      .use {s =>
        IO(s"$s Is A Resource").debug
      }
      .as(ExitCode.Success)

      val stringResource: Resource[IO, String] =
        Resource.make(
          IO(">>> Acquring StringResource :").debug *> IO("String")
        )(_ => IO("<<< Releasing stringResource").debug.void)
}

object BasicResourceFailure extends IOApp{
  def run(args: List[String]): IO[ExitCode] =
    stringResource
      .use(_ => IO.raiseError(new RuntimeException("Basic Resource FAIL")))
      .attempt
      .debug
      .as(ExitCode.Success)

      val stringResource: Resource[IO, String] =
        Resource.make(
          IO(">>> Acquring StringResource :").debug *> IO("String")
        )(_ => IO("<<< Releasing stringResource").debug.void)
}

def psep = println("-" * 30)

println("BasicResource")
BasicResource.run(List.empty[String]).unsafeRunSync
psep

println("BasicResourceFailure")
BasicResourceFailure.run(List.empty[String]).unsafeRunSync
psep
