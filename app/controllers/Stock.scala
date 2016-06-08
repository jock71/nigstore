package controllers

import javax.inject.Inject

import org.slf4j.{Logger, LoggerFactory}
import persistence.MongoCollection
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller}
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.Future

// Reactive Mongo imports
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor

// BSON-JSON conversions/collection
import reactivemongo.play.json._

/**
 * The Stock controllers encapsulates the Rest endpoints and the interaction with the MongoDB, via ReactiveMongo
 * play plugin. This provides a non-blocking driver for mongoDB as well as some useful additions for handling JSon.
 *
 * @see https://github.com/ReactiveMongo/Play-ReactiveMongo
 */
class Stock @Inject()(val reactiveMongoApi: ReactiveMongoApi)
  extends Controller with MongoController with ReactiveMongoComponents {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[Stock])

  /*
   * Get a JSONCollection (a Collection implementation that is designed to work
   * with JsObject, Reads and Writes.)
   * Note that the `collection` is not a `val`, but a `def`. We do _not_ store
   * the collection reference to avoid potential problems in development with
   * Play hot-reloading.
   */
  def storageColl: JSONCollection = db.collection[JSONCollection](MongoCollection.storageEntries)
  def productColl: JSONCollection = db.collection[JSONCollection](MongoCollection.products)

  // ------------------------------------------ //
  // Using case classes + Json Writes and Reads //
  // ------------------------------------------ //

  import models.StorageEntry._
  import models._

  private def findEntries(jObj: JsObject): Action[AnyContent] =
      Action.async {
      val cursor: Cursor[StorageEntry] = storageColl.
        find(jObj).
        // sort them by creation date
        sort(Json.obj("created" -> -1)).
        // perform the query and get a cursor of JsObject
        cursor[StorageEntry]()

      // gather all the JsObjects in a list
      val futureUsersList: Future[List[StorageEntry]] = cursor.collect[List]()

      // transform the list into a JsArray
      val futurePersonsJsonArray: Future[JsArray] = futureUsersList.map { entries =>
          Json.arr(entries)
      }
      // everything's ok! Let's reply with the array
      futurePersonsJsonArray.map {
          entries =>
              entries(0) match {
                  case JsDefined(js) => Ok(js)
                  case _ => NotFound
              }
          //Ok(Json.toJson(entries))
      }
  }

  def findByProductId(productId: String): Action[AnyContent] = {
      val filter = Json.obj(
          "remainingQuantity" -> Json.obj("$gt" -> 0),
          "product._id" -> productId)

      findEntries(filter)
  }

}
