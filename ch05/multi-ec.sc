import $exec.^.catsimp

import debug._

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object ShiftingMuliple extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    (ec("E-1"), ec("E-2")) match {
      case (ec1, ec2) =>
        for {
          _ <- IO("first").debug
          _ <- IO.shift(ec1)
          _ <- IO("second").debug
          _ <- IO.shift(ec2)
          _ <- IO("third").debug
        } yield ExitCode.Success
      }

    def ec(name: String): ExecutionContext =
      ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor { r =>
        val t = new Thread(r, s"pool-$name-thread-1")
        t.setDaemon(true)
        t
      })

}

ShiftingMuliple.run(List.empty[String]).unsafeRunSync
