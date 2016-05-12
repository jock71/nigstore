package models

case class ProductCategory(
                          shortName: String,
                          description: String
                          )

object ProductCategory {
    import play.api.libs.json.Json

    // Generates Writes and Reads for Feed and User thanks to Json Macros
    implicit val userFormat = Json.format[ProductCategory]
}