package controllers

import javax.inject.Inject

import org.slf4j.{Logger, LoggerFactory}
import persistence.MongoCollection
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.Future

// Reactive Mongo imports
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor

// BSON-JSON conversions/collection
import reactivemongo.play.json._

/**
 * The Users controllers encapsulates the Rest endpoints and the interaction with the MongoDB, via ReactiveMongo
 * play plugin. This provides a non-blocking driver for mongoDB as well as some useful additions for handling JSon.
  *
  * @see https://github.com/ReactiveMongo/Play-ReactiveMongo
 */
class Products @Inject()(val reactiveMongoApi: ReactiveMongoApi)
  extends Controller with MongoController with ReactiveMongoComponents {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[Products])

  /*
   * Get a JSONCollection (a Collection implementation that is designed to work
   * with JsObject, Reads and Writes.)
   * Note that the `collection` is not a `val`, but a `def`. We do _not_ store
   * the collection reference to avoid potential problems in development with
   * Play hot-reloading.
   */
  def collection: JSONCollection = db.collection[JSONCollection](MongoCollection.products)

  // ------------------------------------------ //
  // Using case classes + Json Writes and Reads //
  // ------------------------------------------ //

  import models.ProductCategory._
  import models._

  def createProduct = Action.async(parse.json) {
    request =>
    /*
     * request.body is a JsValue.
     * There is an implicit Writes that turns this JsValue as a JsObject,
     * so you can call insert() with this JsValue.
     * (insert() takes a JsObject as parameter, or anything that can be
     * turned into a JsObject using a Writes.)
     */
      request.body.validate[models.Product].map {
        product =>
          collection.insert(product).map {
            lastError =>
              logger.debug(s"Successfully inserted with LastError: $lastError")
              Created(s"Product Created")
          }
      }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def updateProduct(shortName: String, description: String) = Action.async(parse.json) {
    request =>
      request.body.validate[models.Product].map {
        category =>
          // find our product category by shortName and description
          val nameSelector = Json.obj("shortName" -> shortName, "description" -> description)
          collection.update(nameSelector, category).map {
            lastError =>
              logger.debug(s"Successfully updated with LastError: $lastError")
              Created(s"Category Updated")
          }
      }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def findProducts = Action.async {
    // let's do our query
    val cursor: Cursor[models.Product] = collection.
      // find all
      find(Json.obj()).
      // sort them by creation date
      sort(Json.obj("created" -> -1)).
      // perform the query and get a cursor of JsObject
      cursor[models.Product]()

    // gather all the JsObjects in a list
    val futureProductsList: Future[List[models.Product]] = cursor.collect[List]()

    // transform the list into a JsArray
    val futureProductsJsonArray: Future[JsArray] = futureProductsList.map { categories =>
      Json.arr(categories)
    }
    // everything's ok! Let's reply with the array
    futureProductsJsonArray.map {
      products =>
        products(0) match {
          case JsDefined(js) => Ok(js)
          case _ => NotFound
        }
    }
  }

}
