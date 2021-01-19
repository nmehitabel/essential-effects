import $file.^.myio, myio.MyIOPkg._

import scala.concurrent.duration.FiniteDuration

object Timing {

  val clock:MyIO[Long] = MyIO(() => System.currentTimeMillis())

  def time[A](action: MyIO[A]): MyIO[(FiniteDuration, A)] =
    for {
      start <- clock
      ret <- action
      end <- clock
    } yield (FiniteDuration(end - start, scala.concurrent.duration.MILLISECONDS), ret)

}

val timedHello = Timing.time(MyIO.putStr("hello"))

timedHello.unsafeRun() match {
  case (duration, _) => println(s"'hello' took $duration")
}

  
