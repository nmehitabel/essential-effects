import $ivy.{
  `org.typelevel::cats-core:2.3.1`,
  `org.typelevel::cats-effect:2.3.1`
}

import cats.effect._

// helper code from
// https://github.com/inner-product/essential-effects-code/blob/solutions/exercises/src/main/scala/com.innerproduct.ee/

object Colorise {
  def apply(a: Any): String =
    s"${colors(a.hashCode.abs % numColors)}$a${Console.RESET}"

  def reversed(a: Any): String =
    s"${Console.REVERSED}${apply(a)}"

  private val colors = List(
    Console.WHITE,
    Console.BLACK + Console.WHITE_B,
    Console.RED,
    Console.GREEN,
    Console.YELLOW,
    Console.BLUE,
    Console.MAGENTA,
    Console.CYAN
  )
  private val numColors = colors.size - 1
}

case class ThreadName(name: String) extends AnyVal {
  override def toString(): String =
    Colorise.reversed(name)
}

object ThreadName {
  def current(): IO[ThreadName] =
    IO(ThreadName(Thread.currentThread().getName))
}

object debug {

  /** Extension methods for an effect of type `IO[A]`. */
  implicit class DebugHelper[A](ioa: IO[A]) { // <1>

    /** Log the value of the effect along with the thread it was computed on. Logging defaults to `println`. */
    def debug(): IO[A] =
      for {
        a <- ioa
        tn <- ThreadName.current
        _ = println(s"[$tn] $a")
      } yield a
  }
}