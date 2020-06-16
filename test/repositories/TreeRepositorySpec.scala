package repositories

import models.Tree
import org.scalatest._
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api._
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent._
import scala.concurrent.duration._

// 테스트할 때 Asynchronous test를 쓸 때 GuiceOneAppPerSuite와 같이 못 쓰는 이슈
// https://github.com/playframework/scalatestplus-play/issues/112
// GuiceOneAppPerAsyncTest
class TreeRepositorySpec extends TestSuite with GuiceOneAppPerSuite {

  private lazy val appConfig: Map[String, Any] = Map(
    "mongodb.servers" -> List("localhost"),
    "mongodb.port" -> 27017,
    "mongodb.db" -> "haru-test"
  )
  val nestedSuite = new AsyncWordSpec
    with MustMatchers
    with OptionValues
    with WsScalaTestClient {

    implicit val conf: Configuration = fakeApplication().configuration

    val treeRepository = new TreeRepositoryImpl()

    // Fixture
    val tree1: Tree = Tree(List(0), 0, "test1", checked = false, List(Tree(List(0, 0), 1, "test1-1", checked = false, List())))
    val tree2: Tree = Tree(List(1), 0, "test2", checked = false, List())
    val tree3: Tree = Tree(
      List(2),
      0,
      "test3",
      checked = false,
      List(
        Tree(
          List(2, 0),
          1,
          "test3-1",
          checked = false,
          List(Tree(List(2, 0, 0), 2, "test3-1-1", checked = false, List()))
        ),
        Tree(List(2, 1), 1, "test3-2", checked = false, List())
      )
    )

    // How to setUp and tearDown : https://stackoverflow.com/questions/39421757/async-before-and-after-for-creating-and-dropping-scala-slick-tables-in-scalatest

    def setUp(): Future[Unit] = {
      lazy val insertResults = Future.sequence(
        Seq(
          treeRepository.insert(tree1),
          treeRepository.insert(tree2),
          treeRepository.insert(tree3)
        )
      )
      for (_ <- treeRepository.deleteAll; _ <- insertResults) yield ()
    }

    def tearDown(): Future[Unit] = {
      for(_ <- treeRepository.deleteAll) yield ()
    }

    override def withFixture(test: NoArgAsyncTest) = new FutureOutcome(for {
      _ <- setUp()
      result <- super.withFixture(test).toFuture
      _ <- tearDown()
    } yield result)

    "The TreeRepository" should {
      "delete all trees" in {
        for (_ <- treeRepository.deleteAll;
             result <- treeRepository.findAll) yield result.length must equal(0)
      }

      "find all trees that inserted before" in {
        for (result <- treeRepository.findAll) yield {
          result must contain(tree1)
          result must contain(tree2)
          result must contain(tree3)
        }
      }

      "find one tree with id" in {
        val id = List(2, 0, 0)

        for (result <- treeRepository.findOne(id))
          yield {
            result.value must equal(Tree(List(2, 0, 0), 2, "test3-1-1", checked = false, List()))
          }
      }

      "update tree with id" in {
        val toBe = Tree(List(2, 0, 0), 2, "test3-2-update", checked = true, List())

        for (_ <- treeRepository.update(toBe);
             result <- treeRepository.findOne(List(2, 0, 0))) yield result.value must equal(toBe)
      }
    }
  }

  override def fakeApplication(): Application = {
    GuiceApplicationBuilder()
      .configure(appConfig)
      .build
  }

  override def nestedSuites: scala.collection.immutable.IndexedSeq[Suite] = Vector(nestedSuite)
}
