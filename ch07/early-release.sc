import $exec.^.catsimp

import debug._

import scala.io.Source

object Resources {

  case class Config(connectionURL: String)

  object Config {
    def fromSource(source: Source): IO[Config] =
      for {
        config <- IO(Config(source.getLines().next()))
        _ <- IO(s"Config.fromSource : read ${config.connectionURL}").debug
      } yield config
  }

  trait DbConnection {
    def query(sql: String): IO[String] // watevs
  }

  object DbConnection {
    def make(connectionURL: String): Resource[IO, DbConnection] =
      Resource.make(
        IO(s"DbConnection : opening connection to $connectionURL").debug *> IO(
          new DbConnection{
            def query(sql: String): IO[String] = IO(s"""DbConnection : (Results for query "$sql")""")
          }
        )
      ) ( _ => IO(s"DbConnection : Closing connection to $connectionURL").debug.void)
  }
}

object EarlyRelease extends IOApp {

  import Resources._

  def run(args: List[String]): IO[ExitCode] =
    dbConnectionResource.use { conn =>
      conn.query("SELECT * FROM users WHERE id = 12").debug
    }
    .as(ExitCode.Success)

  val dbConnectionResource: Resource[IO, DbConnection] =
    for {
      config <- configResource
      conn <- DbConnection.make(config.connectionURL)
    } yield conn

  // lazy val configResource: Resource[IO, Config] =
  //   for {
  //     source <- sourceResurce
  //     config <- Resource.liftF(Config.fromSource(source))
  //   } yield config
  // to release source resource when value available
  // becomes
  lazy val configResource: Resource[IO, Config] =
    Resource.liftF(sourceResource.use(Config.fromSource))

  lazy val sourceResource: Resource[IO, Source] =
    Resource.make(
      IO(s">>> Opening Source to config").debug *> IO(Source.fromString(config))
      )(source => IO(s"<<< closing source to config").debug *> IO(source.close)
    )

  val config = "exampleConnectionURL"

}

EarlyRelease.run(List.empty[String]).unsafeRunSync
