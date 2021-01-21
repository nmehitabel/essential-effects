import cats.effect.ExitCode
import $exec.^.catsimp

import debug._

import java.util.concurrent.CompletableFuture
import scala.jdk.FunctionConverters._

object AsyncCompletable extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    effect.debug.as(ExitCode.Success)

  val effect: IO[String] = fromCF(IO(cf()))

  def fromCF[A](cfa: IO[CompletableFuture[A]]): IO[A] =
    cfa.flatMap{ fa =>
      IO.async { cb =>
        val handler: (A, Throwable) => Unit = {
          case (a, null) => cb(Right(a))
          case (null, e) => cb(Left(e))
          case (a, e)    => sys.error(s"Completable future must return either value or exception, got both $a , $e")
        }

        fa.handle[Unit](handler.asJavaBiFunction)

        ()
      }
    }

  def cf(): CompletableFuture[String] =
    CompletableFuture.completedFuture("CF Woo!")
    //CompletableFuture.failedFuture(new RuntimeException("CF Failz"))
}

AsyncCompletable.run(List.empty[String]).unsafeRunSync
