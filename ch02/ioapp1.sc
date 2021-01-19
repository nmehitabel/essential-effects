import $file.^.catsimp

import cats.effect._

object HelloWorld { 
  
  def run(args: List[String]): IO[ExitCode] =
    helloWorld.as(ExitCode.Success)

  def helloWorld: IO[Unit] = IO(println("Hello World!"))

}

HelloWorld.run(List.empty[String]).unsafeRunSync()
