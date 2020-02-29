package controllers.tree

import javax.inject.{Inject, Provider}

import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
  * DTO for displaying tree information.
  */
case class TreeResource(id: Seq[Int], level: Int, text: String, checked: Boolean, child: Seq[TreeResource])

object TreeResource {
  /**
    * Mapping to read/write a TreeResource out as a JSON value.
    */
    implicit val format: Format[TreeResource] = Json.format
}


/**
  * Controls access to the backend data, returning [[TreeResource]]
  */
class TreeResourceHandler @Inject()(
//    routerProvider: Provider[TreeRouter],
    treeRepository: TreeRepository)(implicit ec: ExecutionContext) {

//  def create(treeInput: TreeFormInput)(
//      implicit mc: MarkerContext): Future[TreeResource] = {
//    val data = TreeData(TreeId("999"), treeInput.title, treeInput.body)
//    // We don't actually create the tree, so return what we have
//    treeRepository.create(data).map { id =>
//      createTreeResource(data)
//    }
//  }

//  def lookup(id: String)(
//      implicit mc: MarkerContext): Future[Option[TreeResource]] = {
//    val treeFuture = treeRepository.get(TreeId(id))
//    treeFuture.map { maybeTreeData =>
//      maybeTreeData.map { treeData =>
//        createTreeResource(treeData)
//      }
//    }
//  }

  def find(implicit mc: MarkerContext): Future[Iterable[TreeResource]] = {
    treeRepository.list().map { treeDataList =>
      treeDataList.map(treeData => createTreeResource(treeData))
    }
  }

  private def createTreeResource(t: TreeData): TreeResource = {
    TreeResource(t.id, t.level, t.text, t.checked, childDataToResource(t.child))
  }

  private def childDataToResource(t: Seq[TreeData]): Seq[TreeResource ]= {
    t.map(td => TreeResource(td.id, td.level, td.text, td.checked, childDataToResource(td.child)))
  }
}
