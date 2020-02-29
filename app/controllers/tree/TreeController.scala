package controllers.tree

import javax.inject.Inject

import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class TreeFormInput(id: Seq[Int], level: Int, text: String, checked: Boolean, child: Seq[TreeFormInput])

/**
  * Takes HTTP requests and produces JSON.
  */
class TreeController @Inject()(cc: TreeControllerComponents)( implicit ec: ExecutionContext) extends TreeBaseController(cc) {

  private val logger = Logger(getClass)

  def list: Action[AnyContent] = Action.async { implicit request =>
    logger.trace("index: ")
    treeResourceHandler.find.map { trees =>
      Ok(Json.toJson(trees))
    }
  }
}
