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
class BackDoor @Inject() (val reactiveMongoApi: ReactiveMongoApi)
  extends Controller with MongoController with ReactiveMongoComponents {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[BackDoor])

  /*
   * Get a JSONCollection (a Collection implementation that is designed to work
   * with JsObject, Reads and Writes.)
   * Note that the `collection` is not a `val`, but a `def`. We do _not_ store
   * the collection reference to avoid potential problems in development with
   * Play hot-reloading.
   */
  def collection: JSONCollection = db.collection[JSONCollection]("storageEntries")

  // ------------------------------------------ //
  // Using case classes + Json Writes and Reads //
  // ------------------------------------------ //

  import models.StorageEntry._
  import models._

  def resetDb = Action.async(parse.json) {
    request =>
      db.drop()
      Future.successful(Created("Database created"))
  }

    def initDb = Action.async(parse.json) {
        request =>
            val entries:JSONCollection = db.collection[JSONCollection](MongoCollection.storageEntries)
            val demoEntries = List(
                StorageEntry("1", "Siringhe 20cc", "20160502", "20170502", "12345", 20, 20, List())
            )
            //import demoEntries.ImplicitlyDocumentProducer
            //val bulkDocs = // prepare the person documents to be inserted
            //    demoEntries.map(implicitly[collection.ImplicitlyDocumentProducer](_))
            //entries.bulkInsert(ordered=true)(bulkDocs: _*).map {
            entries.insert(demoEntries(0)).map {
                lastError =>
                    Created(s"StorageEntry Created")
            }
    }


}
