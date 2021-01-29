import $exec.^.catsimp

import cats.effect.concurrent._
import cats.implicits._
import scala.concurrent.duration._
import debug._

object LatchData {

  sealed trait State extends Product with Serializable
  case class Outstanding(n: Long, whenDone: Deferred[IO, Unit]) extends State
  case class Done() extends State


  trait CountdownLatch {
    def await(): IO[Unit]
    def decrement(): IO[Unit]
  }

  object CountdownLatch {
    def apply(n: Long)(implicit cs: ContextShift[IO]): IO[CountdownLatch] =
      for {
        whenDone <- Deferred[IO, Unit]
        state <- Ref[IO].of[State](Outstanding(n, whenDone))
      } yield new CountdownLatch {
          def await(): IO[Unit] =
            state.get.flatMap{
              case Outstanding(_, whenDone) => whenDone.get
              case Done() => IO.unit
            }

          def decrement(): IO[Unit] =
            state.modify {
              case Outstanding(1, whenDone) => Done() -> whenDone.complete(())
              case Outstanding(n, whenDone) =>
                Outstanding(n - 1, whenDone) -> IO.unit
              case Done() => Done() -> IO.unit
            }.flatten
      }
  }
}

object LatchStateMachine extends IOApp {
  import LatchData._

  def run(args: List[String]): IO[ExitCode] =
    for {
      latch <- CountdownLatch(13)
      _ <- (beeper(latch), tickingClock(latch)).parTupled
    } yield ExitCode.Success

  def beeper(latch: CountdownLatch) =
    for {
      _ <- latch.await
      _ <- IO("Latch Beep!!!").debug
    } yield()

  def tickingClock(latch: CountdownLatch): IO[Unit] =
    for {
      _ <- IO.sleep(1.second)
      _ <- IO(System.currentTimeMillis).debug
      _ <- latch.decrement
      _ <- tickingClock(latch)
    } yield ()

}

LatchStateMachine.run(List.empty[String]).unsafeRunSync