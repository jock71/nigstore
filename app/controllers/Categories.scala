package controllers

import javax.inject.Inject

import org.slf4j.{LoggerFactory, Logger}
import persistence.MongoCollection
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.Future

import play.api.mvc.{ Action, Controller }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json._

// Reactive Mongo imports
import reactivemongo.api.Cursor

import play.modules.reactivemongo.{ // ReactiveMongo Play2 plugin
MongoController,
ReactiveMongoApi,
ReactiveMongoComponents
}

// BSON-JSON conversions/collection
import reactivemongo.play.json._

/**
 * The Users controllers encapsulates the Rest endpoints and the interaction with the MongoDB, via ReactiveMongo
 * play plugin. This provides a non-blocking driver for mongoDB as well as some useful additions for handling JSon.
  *
  * @see https://github.com/ReactiveMongo/Play-ReactiveMongo
 */
class Categories @Inject() (val reactiveMongoApi: ReactiveMongoApi)
  extends Controller with MongoController with ReactiveMongoComponents {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[Categories])

  /*
   * Get a JSONCollection (a Collection implementation that is designed to work
   * with JsObject, Reads and Writes.)
   * Note that the `collection` is not a `val`, but a `def`. We do _not_ store
   * the collection reference to avoid potential problems in development with
   * Play hot-reloading.
   */
  def collection: JSONCollection = db.collection[JSONCollection]("categories")

  // ------------------------------------------ //
  // Using case classes + Json Writes and Reads //
  // ------------------------------------------ //

  import models.ProductCategory._
  import models._

  def createCategory = Action.async(parse.json) {
    request =>
    /*
     * request.body is a JsValue.
     * There is an implicit Writes that turns this JsValue as a JsObject,
     * so you can call insert() with this JsValue.
     * (insert() takes a JsObject as parameter, or anything that can be
     * turned into a JsObject using a Writes.)
     */
      request.body.validate[ProductCategory].map {
        category =>
        // `user` is an instance of the case class `models.User`
          collection.insert(category).map {
            lastError =>
              logger.debug(s"Successfully inserted with LastError: $lastError")
              Created(s"ProductCategory Created")
          }
      }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def updateCategory(shortName: String, description: String) = Action.async(parse.json) {
    request =>
      request.body.validate[ProductCategory].map {
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

  def findCategories = Action.async {
    // let's do our query
    val cursor: Cursor[ProductCategory] = collection.
      // find all
      find(Json.obj()).
      // sort them by creation date
      sort(Json.obj("created" -> -1)).
      // perform the query and get a cursor of JsObject
      cursor[ProductCategory]()

    // gather all the JsObjects in a list
    val futureCategoriesList: Future[List[ProductCategory]] = cursor.collect[List]()

    // transform the list into a JsArray
    val futureCategoriesJsonArray: Future[JsArray] = futureCategoriesList.map { categories =>
      Json.arr(categories)
    }
    // everything's ok! Let's reply with the array
    futureCategoriesJsonArray.map {
      categories =>
        categories(0) match {
          case JsDefined(js) => Ok(js)
          case _ => NotFound
        }
    }
  }

}
