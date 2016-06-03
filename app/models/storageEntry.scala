package models

import java.util.Date

case class Picking(pickId: String,
                   usageDate: Date,
                   quantity: Int,
                   usage: Option[String]
                  )

case class StorageEntry(
                       opId: String,
                       product: Product,
                       arrivalDate: Date,
                       expireDate: Date,
                       lot: String,
                       initialQuantity: Int,
                       remainingQuantity: Int,
                       pickings: List[Picking]
                       )

object StorageEntry {
    import play.api.libs.json.Json

    // Generates Writes and Reads for Feed thanks to Json Macros
    implicit val pickingFormat = Json.format[Picking]
    implicit val storageEntryFormat = Json.format[StorageEntry]
}