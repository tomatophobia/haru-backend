package controllers

import javax.inject.Inject

import play.api.Logger
import play.api.libs.json.Json.toJson
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

import services.TreeService

import models.Tree

/**
  * Takes HTTP requests and produces JSON.
  */
class TreeController @Inject() (cc: ControllerComponents, treeService: TreeService)(
    implicit ec: ExecutionContext
) extends AbstractController(cc) {

  private val logger = Logger(getClass)

  def findAll: Action[AnyContent] = Action.async { implicit request =>
    logger.trace("findAll: ")
    treeService.findAll().map { trees =>
      logger.trace(s"Found ${trees.size} trees.")
      Ok(toJson(trees))
    }
  }

  def update(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    logger.trace("update: ")
    treeService.update(request.body.as[Tree]) map (_ => Ok)
  }
}
