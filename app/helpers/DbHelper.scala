package helpers

import play.api.Configuration
import play.api.ConfigLoader
import play.api.ConfigLoader.configLoader

import reactivemongo.api.AsyncDriver
import reactivemongo.api.bson.collection.BSONCollection

import scala.concurrent.{ExecutionContext, Future}
import scala.collection.JavaConverters._

import com.typesafe.config.Config

object DbHelper {
  def getCollection(
      collectionName: String
  )(implicit config: Configuration, ec: ExecutionContext): Future[BSONCollection] = {
    val mongoConfig = config.get[MongoConfig]("mongodb")
    var hosts = mongoConfig.servers
    val port = mongoConfig.port
    val uris = hosts.map(host => s"$host:$port")
    val dbName = mongoConfig.db

    new AsyncDriver()
      .connect(uris)
      .flatMap(_.database(dbName))
      .map(_.collection(collectionName))
  }

  case class MongoConfig(db: String, servers: List[String] , port: Int)
  object MongoConfig {
    implicit val configLoader: ConfigLoader[MongoConfig] = new ConfigLoader[MongoConfig] {
      def load(rootconfig: Config, path: String): MongoConfig = {
        val config = rootconfig.getConfig(path)
        MongoConfig(
          config.getString("db"),
          config.getStringList("servers").asScala.toList,
          config.getInt("port")
        )
      }
    }
  }
}
