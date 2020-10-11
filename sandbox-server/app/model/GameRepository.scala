package model

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


class GameRepository @Inject() (val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val Games = TableQuery[GameTable]

  private class GameTable(tag: Tag) extends Table[Game](tag, "Games") {
    def id   = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def *    = (id, name) <> (Game.tupled, Game.unapply)
  }

  def allGames(): Future[Seq[Game]] = db.run(Games.result)
  def insertGame(name: String): Future[Boolean] = db.run(Games += Game(1, name)).map(_ > 0)
  def getGame(id: Int): Future[Option[Game]] = db.run(Games.filter(_.id === id).result.headOption)
  def updateGame(game: Game): Future[Option[Game]] = db.run(Games.filter(_.id === game.id).update(game).map(updates => if (updates > 0) Some(game) else None))
  def deleteGame(id: Int): Future[Boolean] = db.run(Games.filter(_.id === id).delete.map(_ > 0))

}