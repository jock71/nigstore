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
class StorageEntries @Inject() (val reactiveMongoApi: ReactiveMongoApi)
  extends Controller with MongoController with ReactiveMongoComponents {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[StorageEntries])

  /*
   * Get a JSONCollection (a Collection implementation that is designed to work
   * with JsObject, Reads and Writes.)
   * Note that the `collection` is not a `val`, but a `def`. We do _not_ store
   * the collection reference to avoid potential problems in development with
   * Play hot-reloading.
   */
  def collection: JSONCollection = db.collection[JSONCollection](MongoCollection.storageEntries)

  // ------------------------------------------ //
  // Using case classes + Json Writes and Reads //
  // ------------------------------------------ //

  import models.JsonFormats._
  import models.StorageEntry._
  import models._

  def createEntry = Action.async(parse.json) {
    request =>
    /*
     * request.body is a JsValue.
     * There is an implicit Writes that turns this JsValue as a JsObject,
     * so you can call insert() with this JsValue.
     * (insert() takes a JsObject as parameter, or anything that can be
     * turned into a JsObject using a Writes.)
     */
      request.body.validate[StorageEntry].map {
        entry =>
          collection.insert(entry).map {
            lastError =>
              logger.debug(s"Successfully inserted with LastError: $lastError")
              Created(s"StorageEntry Created")
          }
      }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def removeEntry(opId: String) = Action.async(parse.json) {
    request =>
          val idSelector = Json.obj("opId" -> opId)
          collection.remove(idSelector).map {
            msg =>
              println(s"$msg")
              Ok

          }
  }

  def updateEntry(opId: String) = Action.async(parse.json) {
    request =>
      request.body.validate[StorageEntry].map {
        entry =>
          val selector = Json.obj("opId" -> opId)
          collection.update(selector, entry).map {
            lastError =>
              logger.debug(s"Successfully updated with LastError: $lastError")
              Created(s"Entry Updated")
          }

      }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def pickUp(opId: String) = Action.async(parse.json) {
    request =>
      logger.info(s"pickUp request for $opId")
      request.body.validate[Picking].map {
          picking:Picking =>
              logger.info(s"valid picking=$picking")
              val selector = Json.obj("opId" -> opId)
              val cursor = collection.find(selector).cursor[StorageEntry]()
              val fEntry = cursor.collect[List](1)
              fEntry.map {
                  case List(entry:StorageEntry) =>
                      logger.info(s"picking requested=${picking.quantity}, available=${entry.remainingQuantity}")
                      if(picking.quantity <= entry.remainingQuantity) {
                          val updatedEntry = entry.copy(
                              remainingQuantity = entry.remainingQuantity-picking.quantity,
                              pickings = entry.pickings ::: List(picking)
                          )
                          collection.update(selector, updatedEntry).map {
                              lastError =>
                                  logger.debug(s"pickup added lastError:$lastError")
                                  Ok
                          }
                      }
                      else {
                          Future.successful(PreconditionFailed)
                      }
                      Ok
              }

      }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def findEntries = Action.async {
    // let's do our query
    logger.info("findEntries")
    val cursor: Cursor[StorageEntry] = collection.
      // find all
      find(Json.obj()).
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

}
