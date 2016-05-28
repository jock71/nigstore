package models

case class Product(
                  name: String,
                  description: String,
                  category: ProductCategory,
                  orderedThrough: String,
                  producer: String,
                  distributor: String,
                  code: String,
                  internalCode: String,
                  packaging: String,
                  itemsPerPackage: Int,
                  reorderThreshold: Int
                  )

object Product {
    import play.api.libs.json.Json

    // Generates Writes and Reads for Feed thanks to Json Macros
    implicit val categoryFormat = Json.format[ProductCategory]
    implicit val productFormat = Json.format[Product]
}