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


class TreeRepositorySpec extends PlaySpec with GuiceOneAppPerTest {
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


  "The TreeRepository" should {
    "find all trees" in {
      implicit val conf = app.configuration
      val treeRepository = new TreeRepositoryImpl()
      val initialTree = Tree(List(0), 0, "test", false, List())
      treeRepository.insert(initialTree)

      val result: List[Tree] = Await.result(treeRepository.findAll, 10.seconds)

      result.head must equal (initialTree)
    }
  } 
}

