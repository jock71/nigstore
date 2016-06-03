package models

case class Product(
                  name: String,
                  description: String,
                  category: ProductCategory,
                  orderedThrough: Option[String],
                  producer: String,
                  distributor: Option[String],
                  code: String,
                  internalCode: Option[String],
                  packaging: Option[String],
                  itemsPerPackage: Int,
                  reorderThreshold: Int
                  )

object Product {
    import play.api.libs.json.Json

    // Generates Writes and Reads for Feed thanks to Json Macros
    implicit val categoryFormat = Json.format[ProductCategory]
    implicit val productFormat = Json.format[Product]
}