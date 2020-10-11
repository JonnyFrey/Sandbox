package controllers

import javax.inject.Inject
import model._
import play.api.libs.json._
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

//noinspection TypeAnnotation
class GameTrackerController @Inject()(gameRepo: GameRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  def index = Action {
    ok("Pong")
  }

  def listGames = Action.async {
    gameRepo.allGames().map(ok(_))
  }

  def getGame(id: Int) = Action.async {
    gameRepo.getGame(id).map {
      case Some(value) => ok(value)
      case None        => error(s"There is no game at $id", NotFound)
    }
  }

  def addGame(game: String) = Action.async {
    if (game == null || game.length < 1) {
      Future {
        error("Game name string can't be null or empty", BadRequest)
      }
    } else {
      gameRepo.insertGame(game).map { isSuccessful =>
        if (isSuccessful) {
          ok("acknowledge")
        } else {
          error(s"Could not add game with name $game", InternalServerError)
        }
      }
    }
  }

  def updateGame(id: Int, name: String) = Action.async {
    gameRepo.updateGame(Game(id, name)).map {
      case Some(value) => ok(value)
      case None        => error(s"There is no game to update at $id", NotFound)
    }
  }

  def deleteGame(id: Int) = Action.async {
    gameRepo.deleteGame(id).map { isSuccessful =>
      if (isSuccessful) {
        ok("acknowledge")
      } else {
        error(s"There is no game to remove at $id", NotFound)
      }
    }
  }

  def ok[T](o: T)(implicit tjs: Writes[T]) = Ok(body(isSuccessful = true, value = Json.toJson(o))).as(JSON)

  def error(message: String, f: Status) = f(body(isSuccessful = false, errorMessage = JsString(message))).as(JSON)

  def body(isSuccessful: Boolean, errorMessage: JsValue = JsNull, value: JsValue = JsNull) = JsObject(Seq(
    "isSuccessful" -> JsBoolean(isSuccessful),
    "errorMessage" -> errorMessage,
    "body" -> value
  ))

}
