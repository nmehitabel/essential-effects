import $exec.^.catsimp

import debug._

import java.io._

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

class FileBufferReader private(in: RandomAccessFile) {
  def readBuffer(offset: Long): IO[(Array[Byte], Int)] =
    IO {
      in.seek(offset)
      val buf = new Array[Byte](FileBufferReader.bufferSize)
      val len = in.read(buf)

      (buf, len)
    }

  private def close: IO[Unit] = IO(in.close())
}

object FileBufferReader {
  val bufferSize = 160 // deliberately small

  def makeResource(fileName: String): Resource[IO, FileBufferReader] =
    Resource.make{
      IO(new FileBufferReader(new RandomAccessFile(fileName,"r")))
    } { res =>
      res.close
    }
}


object ResourceRead extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    args.headOption match {
      case Some(name) =>
        FileBufferReader.makeResource(name).use {rf =>
            rf.readBuffer(124).map {
              case (buf, _) => { // should start with "0030" and and at "0070"
                val s = (buf.map(_.toChar)).mkString
                s
              }
            }
          }.attempt.debug.as(ExitCode.Success)
      case None =>
        IO(System.err.println("Error filename arument required")).as(ExitCode(2))
    }
}


def psep = println("-" * 30)

val fname = "foo.tmp"

def writeFileStr(fname: String,str: String): Unit = {
  val file = new File(fname)
  val bw = new BufferedWriter(new FileWriter(fname))
  bw.write(str)
  bw.close()
  ()
}


println("BasicResource")
BasicResource.run(List.empty[String]).unsafeRunSync
psep

println("BasicResourceFailure")
BasicResourceFailure.run(List.empty[String]).unsafeRunSync
psep

println("ResourceRead")
val fourds = (0 to 999).map(d => f"$d%04d").mkString("")
writeFileStr(fname, fourds)
ResourceRead.run(List(fname)).unsafeRunSync
psep
