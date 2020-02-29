package controllers.tree

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.{Future, ExecutionContext}

final case class TreeData(id: Seq[Int], level: Int, text: String, checked: Boolean, child: Seq[TreeData])

// class TreeExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")
// 커스텀 TreeExecutionContext 왜 안되는지 모르겠음 ㅎ

/**
  * A pure non-blocking interface for the TreeRepository.
  */
trait TreeRepository {
//  def create(data: TreeData)(implicit mc: MarkerContext): Future[TreeId]

  def list()(implicit mc: MarkerContext): Future[Iterable[TreeData]]

//  def get(id: TreeId)(implicit mc: MarkerContext): Future[Option[TreeData]]
}

/**
  * A trivial implementation for the Tree Repository.
  *
  * A custom execution context is used here to establish that blocking operations should be
  * executed in a different thread than Play's ExecutionContext, which is used for CPU bound tasks
  * such as rendering.
  */
@Singleton
class TreeRepositoryImpl @Inject()()(implicit ec: ExecutionContext) extends TreeRepository {

  private val logger = Logger(this.getClass)

  private val treeList = List(
    TreeData(List(0), 0, "하루 프로젝트 시작하기", false, List(TreeData(List(0, 0), 1, "프론트앤드 완성", false, List()), TreeData(List(0, 1), 1, "백앤드 완성", false, List())))
  )

  override def list()(implicit mc: MarkerContext): Future[Iterable[TreeData]] = {
    Future {
      logger.trace(s"list: ")
      treeList
    }
  }

//  override def get(id: TreeId)(
//      implicit mc: MarkerContext): Future[Option[TreeData]] = {
//    Future {
//      logger.trace(s"get: id = $id")
//      treeList.find(tree => tree.id == id)
//    }
//  }
//
//  def create(data: TreeData)(implicit mc: MarkerContext): Future[TreeId] = {
//    Future {
//      logger.trace(s"create: data = $data")
//      data.id
//    }
//  }

}
