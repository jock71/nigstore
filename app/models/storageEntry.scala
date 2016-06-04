package models

import java.util.Date

import reactivemongo.bson.BSONObjectID

case class Picking(pickId: String,
                   usageDate: Date,
                   quantity: Int,
                   usage: Option[String]
                  )

case class StorageEntry(
                       product: Product,
                       arrivalDate: Date,
                       expireDate: Date,
                       lot: String,
                       initialQuantity: Int,
                       remainingQuantity: Int,
                       pickings: List[Picking],
                       _id: BSONObjectID = BSONObjectID.generate
                       )

object StorageEntry {
    import play.api.libs.json.Json

    // Generates Writes and Reads for Feed thanks to Json Macros
    implicit val pickingFormat = Json.format[Picking]
    implicit val storageEntryFormat = Json.format[StorageEntry]
}