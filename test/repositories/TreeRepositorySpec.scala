package repositories

import org.scalatest._
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
import play.api._
import play.api.http.MimeTypes
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import java.io.File
import models.Tree
import injection.TestModule


class TreeRepositorySpec extends PlaySpec with GuiceOneAppPerTest with BeforeAndAfter {
  private lazy val appConfig: Map[String, Any] = Map(
    "mongodb.servers" -> List("localhost"),
    "mongodb.port" -> 27017,
    "mongodb.db" -> "haru-test"
  )

  override def fakeApplication(): Application = {
    GuiceApplicationBuilder()
    .configure(appConfig)
    .build
  }

  implicit val conf = fakeApplication.configuration
  val treeRepository = new TreeRepositoryImpl()

  before {
    val initialTree = Tree(List(0), 0, "test", false, List())
    treeRepository.insert(initialTree)
  }

  after {
    treeRepository.deleteAll
  }


  "The TreeRepository" should {
    "find all trees" in {
      val result: List[Tree] = Await.result(treeRepository.findAll, 10.seconds)

      result.head must equal (Tree(List(0), 0, "test", false, List()))
    }
  } 
}

