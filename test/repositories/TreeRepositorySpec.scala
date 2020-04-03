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
  val tree1 = Tree(List(0), 0, "test1", false, List())
  val tree2 = Tree(List(1), 0, "test2", false, List())
  val tree3 = Tree(List(2), 0, "test3", false, List())

//  before {
//  }

  after {
    treeRepository.deleteAll
  }

  "The TreeRepository" should {
    "delete all trees" in {
      for (_ <- treeRepository.deleteAll;
           result <- treeRepository.findAll) yield result.length must equal(0)
    }

    "find all trees that inserted before" in {

      val insertResults = Future.sequence(
        Seq(
          treeRepository.insert(tree1),
          treeRepository.insert(tree2),
          treeRepository.insert(tree3)
        )
      )

      for (_ <- insertResults;
           result <- treeRepository.findAll) yield {
        result must contain(tree1)
        result must contain(tree2)
        result must contain(tree3)
      }
    }
  }
}
