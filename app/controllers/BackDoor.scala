package controllers

import javax.inject.Inject

import org.slf4j.{LoggerFactory, Logger}
import persistence.MongoCollection
import reactivemongo.api.commands.MultiBulkWriteResult
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.Future

import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext


import play.modules.reactivemongo.{// ReactiveMongo Play2 plugin
MongoController,
ReactiveMongoApi,
ReactiveMongoComponents
}


/**
  * The Users controllers encapsulates the Rest endpoints and the interaction with the MongoDB, via ReactiveMongo
  * play plugin. This provides a non-blocking driver for mongoDB as well as some useful additions for handling JSon.
  *
  * @see https://github.com/ReactiveMongo/Play-ReactiveMongo
  */
class BackDoor @Inject()(val reactiveMongoApi: ReactiveMongoApi)
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
            val fResults: Seq[Future[MultiBulkWriteResult]] = List(
                insertCategories(),
                insertProducts(),
                insertEntries()
            )

            val fList:Future[Seq[MultiBulkWriteResult]] = Future.sequence(fResults)

            fList.map {
                case result =>
                    Created(s"Storage initialized properly $result")

            }
    }

    val f = new java.text.SimpleDateFormat("yyyy-MM-dd")
    private final val catPlastic = ProductCategory("Plastiche", "materiale plastico")

    private def insertCategories():Future[MultiBulkWriteResult]  = {
        val collection: JSONCollection = db.collection[JSONCollection](MongoCollection.categories)
        val categories = Seq(
            catPlastic,
            ProductCategory("Reagenti", "reagenti")

        )
        val bulkDocs = categories.map(implicitly[collection.ImplicitlyDocumentProducer](_))
        val bulkResult = collection.bulkInsert(ordered = true)(bulkDocs: _*)
        bulkResult
        //insertAll(collection, categories)
    }

    private final val prodTermo = Product("Termoprobe", "Termoprobe", catPlastic, "-", "Biorep", "Biorep", "TC-02", "", "1/pkg", 1, 2)
    private final val prodSir10 = Product("Siringa", "Siringhe 10ml", catPlastic, "-", "Artsana PIC", "Artsana PIC", "03.076000.300.310", "", "1/pkg", 1, 20 )
    private final val prodPunch = Product("Punch", "Punch 50 ml", catPlastic, "-", "Euroclone", "Corning", "4501", "", "100 pz tot, 25 pz/b, 4 b/cartone", 100, 2)
    private def insertProducts():Future[MultiBulkWriteResult]  = {
        val collection: JSONCollection = db.collection[JSONCollection](MongoCollection.products)
        val products = Seq(
            prodTermo,
            prodSir10,
            prodPunch
        )
        val bulkDocs = products.map(implicitly[collection.ImplicitlyDocumentProducer](_))
        val bulkResult = collection.bulkInsert(ordered = true)(bulkDocs: _*)
        bulkResult
        //insertAll(collection, categories)
    }

    private def insertEntries(): Future[MultiBulkWriteResult]  = {
        val collection: JSONCollection = db.collection[JSONCollection](MongoCollection.storageEntries)

        val entries: Seq[StorageEntry] = demoEntries ::: plasticList
        //insertAll(collection, demoEntries)
        val bulkDocs = demoEntries.map(implicitly[collection.ImplicitlyDocumentProducer](_))
        val bulkResult = collection.bulkInsert(ordered = true)(bulkDocs: _*)
        bulkResult
    }

    private val demoEntries:List[StorageEntry] = List(
        StorageEntry("1", "Siringhe 20cc", f.parse("2016-05-02"), f.parse("2017-05-02"), "12345", 20, 0,
            List(
                Picking("1", f.parse("2016-04-10"), 10),
                Picking("2", f.parse("2016-05-20"), 10)
            )),
        StorageEntry("2", "Siringhe 10cc", f.parse("2016-05-02"), f.parse("2017-05-02"), "12345", 20, 20, List()),
        StorageEntry("3", "Siringhe 1cc", f.parse("2016-05-02"), f.parse("2017-05-02"), "12345", 20, 20, List()),
        StorageEntry("4", "Siringhe 2cc", f.parse("2016-05-02"), f.parse("2017-05-02"), "12345", 20, 20, List()),
        StorageEntry("5", "Siringhe 2.5cc", f.parse("2016-05-02"), f.parse("2017-05-02"), "12345", 20, 20, List()),
        StorageEntry("6", "Siringhe 5cc", f.parse("2016-05-02"), f.parse("2017-05-02"), "12345", 20, 20, List())
    )

    private val plasticList:List[StorageEntry] = {
        /*
        LOTTO	    SCADENZA	n° PEZZI	DATA ARRIVO
        A19872-1	ott-15	    25	        02/01/2013
        A21986-1	feb-16	    30	        18/04/14
        A21986-3	giu-17	    30	        06/03/15
        A21986-5	mag-18	    35	        11/01/16
        A21986-5	mag-18	    35	        04/04/16
         */
        List(
            StorageEntry("p1", "product", f.parse("2013-01-02"), f.parse("2015-10-01"), "A19872-1", 25, 25, List()),
            StorageEntry("p2", "product", f.parse("2014-04-18"), f.parse("2016-02-16"), "A21986-1", 30, 30, List()),
            StorageEntry("p3", "product", f.parse("2015-03-06"), f.parse("2017-06-01"), "A21986-3", 30, 30, List()),
            StorageEntry("p4", "product", f.parse("2016-01-11"), f.parse("2018-05-01"), "A21986-5", 35, 35, List()),
            StorageEntry("p5", "product", f.parse("2016-04-04"), f.parse("2018-05-01"), "A21986-5", 35, 35, List())
        )
    }

//    private def insertAll[T](collection:JSONCollection, entries: Seq[T] ): Future[MultiBulkWriteResult] = {
//        val bulkDocs = entries.map(implicitly[collection.ImplicitlyDocumentProducer](_))
//        val bulkResult = collection.bulkInsert(ordered = true)(bulkDocs: _*)
//        bulkResult
//    }


}
