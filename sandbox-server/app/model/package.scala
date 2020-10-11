import play.api.libs.json.{Json, OFormat}
import play.api.libs.json._

package object model {

  implicit val gameFormat: OFormat[Game] = Json.format[Game]

  case class Game(id: Int, name: String)

}
