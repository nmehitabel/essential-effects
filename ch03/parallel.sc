import $exec.^.catsimp

import cats.implicits._

import debug._

object RunMapN extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    seq.as(ExitCode.Success)

  val hello = IO("Hello!").debug
  val world = IO("World!").debug

val seq = (hello, world).mapN((h, w) => s"SEQ => $h $w").debug
}

object RunParMapN extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    par.as(ExitCode.Success)

  val hello = IO("Hello!").debug
  val world = IO("World!").debug

  val par = (hello, world).parMapN((h, w) => s"PAR => $h $w").debug
}

object RunParMapNErrors extends IOApp {
  import scala.concurrent.duration._

  def run(args: List[String]): IO[ExitCode] =
    e1.attempt.debug   *>
    IO("------").debug *>
    e2.attempt.debug   *>
    IO("------").debug *>
    e3.attempt.debug   *>
    IO.pure(ExitCode.Success)

  val ok = IO("hi").debug
  val ko1 = IO.sleep(1.second).as("ko1").debug *> IO.raiseError[String](new RuntimeException("oh!"))
  val ko2 = IO.raiseError[String](new RuntimeException("noes!"))

  val e1 = (ok, ko1).parMapN((_, _) => ())
  val e2 = (ko1, ok).parMapN((_, _) => ())
  // cleaner code with parTupled.void to same effect
  val e3 = (ko1, ko2).parTupled.void

}
def psep = println("-" * 30)
println("RunMapN")
RunMapN.run(List.empty[String]).unsafeRunSync
psep
println("RunParMapN")
RunParMapN.run(List.empty[String]).unsafeRunSync
psep
println("RunParMapNErrors")
RunParMapNErrors.run(List.empty[String]).unsafeRunSync
psep