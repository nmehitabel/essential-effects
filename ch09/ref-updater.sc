import $exec.^.catsimp

import cats.effect.concurrent.Ref
import cats.implicits._

import debug._

object RefUpdateImpure extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    for {
      ref <- Ref[IO].of(0)
      _ <- List(1, 2, 3).parTraverse(task(_, ref))
    } yield ExitCode.Success

  def task(id: Int, ref: Ref[IO, Int]): IO[Unit] =
    ref
      .modify(prev => id ->  IO(s"$prev -> $id").debug)
      .flatten
      .replicateA(3)
      .void
}

RefUpdateImpure.run(List.empty[String]).unsafeRunSync