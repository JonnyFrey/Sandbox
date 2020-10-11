package controllers

import controllers.App.board
import javax.inject.{Inject, Singleton}
import net.zomis.minesweeper.analyze.factory.General2DAnalyze
import play.api.i18n.{Lang, MessagesApi}
import play.api.libs.json.{JsNull, JsObject, JsPath, JsValue, Json, JsonValidationError, OFormat, Writes}
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents, MessagesRequest, Result}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._

@Singleton
class MinesweeperController @Inject()(cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  implicit val heatMapFormat: OFormat[HeatMap] = Json.format[HeatMap]
  implicit val probsFormat: OFormat[Probability] = Json.format[Probability]

  def heatmap() = Action.async(parse.json) { implicit request =>
    request.body.validate[HeatMap].fold(
      invalidRequest => Future { badRequest(invalidRequest)},
      validRequest => {
        Future {
          println(validRequest.map.toSeq + " - " + validRequest.bombs)
          val analyze = new General2DAnalyze(validRequest.map, validRequest.bombs)
          Ok(Json.toJson(
            analyze.solve().analyzeDetailed(analyze).getProxies.asScala.toSeq.map {
              element => Probability(element.getField.getY, element.getField.getX, element.getMineProbability)
            }
          )).as(JSON)
        }
      }
    )
  }

  def badRequest(failures: collection.Seq[(JsPath, collection.Seq[JsonValidationError])])(implicit request: MessagesRequest[JsValue], writes: Writes[JsValue]): Result = {
    val readableFailures: Seq[JsObject] = failures.map { case (path, pathErrors) =>
      Json.obj(
        "field" -> path.toJsonString.stripPrefix("obj."),
        "value" -> path.asSingleJson(request.body).getOrElse(JsNull),
        "failures" -> extractErrors(pathErrors.toSeq)
      )
    }.toSeq

    BadRequest(
      Json.obj(
        "user_submitted_json" -> request.body,
        "validations" -> readableFailures
      )
    ).as(JSON)
  }

  /** Extracts all failures for a given field */
  private def extractErrors[A](errors: Seq[JsonValidationError])(implicit request: MessagesRequest[A], writes: Writes[A]): Seq[String] = {
    errors.flatMap(translateMessages(_)(request.messagesApi, request.messages.lang)).distinct
  }

  /** Extracts human-readable error messages from a given [[JsonValidationError]] */
  private def translateMessages(failure: JsonValidationError)(implicit messages: MessagesApi, lang: Lang): Seq[String] = failure.args match {
    case a if a.isEmpty   => failure.messages.map(messages(_))
    case a if a.nonEmpty  => Seq(messages(failure.message, a:_*))
  }
}

case class HeatMap(map: Array[String], bombs: Int)
case class Probability(x: Int, y: Int, chance: Double)

object App extends App {

  val start = System.currentTimeMillis()




  val board = List(
    "002?200000",
    "002?211100",
    "122????100",
    "?????31100",
    "?????20000",
    "??11110000",
    "??20001110",
    "??20001?10"
  ).toArray

  val analyze = new General2DAnalyze(board, 10)

  val solve = analyze.solve()

  val details = solve.analyzeDetailed(analyze)



  val map: Map[(Int, Int), Double] = details.getProxies.asScala.map(ele => ((ele.getField.getY, ele.getField.getX), ele.getMineProbability)).toMap

  val newBoard = board.map(_.zipWithIndex).zipWithIndex.map {
    case (row, rowIdx) => row.map {
      case (col, colIdx) => if (col == '?') s"[${map.getOrElse((rowIdx, colIdx), "?")}]" else col.toString
    }.toString()
  }.toSeq

  println(map)
  println(newBoard.mkString("\n"))

  println(System.currentTimeMillis() - start)
}