package models

import reactivemongo.bson.BSONObjectID

case class ProductCategory(
                          shortName: String,
                          description: String,
                          _id: BSONObjectID = BSONObjectID.generate
                          )

object ProductCategory {
    import play.api.libs.json.Json

    // Generates Writes and Reads for Feed and User thanks to Json Macros
    implicit val userFormat = Json.format[ProductCategory]
}