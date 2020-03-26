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

  private val logger = Logger(this.getClass)

  def findAll: Action[AnyContent] = Action.async { implicit request =>
    logger.info("findAll: ")
    treeService.findAll.map { trees =>
      logger.debug(s"Found ${trees.size} trees.")
      Ok(toJson(trees))
    }
  }

  def insert: Action[JsValue] = Action.async(parse.json) { implicit request =>
    logger.info("insert: ")
    treeService.insert(request.body.as[Tree]).map (_ => Ok)
  }

  def update(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    logger.info("update: ")
    treeService.update(request.body.as[Tree]) map (_ => Ok)
  }

  def delete(id: Seq[Int]): Action[AnyContent] = Action.async { implicit request =>
    logger.info("delete: ")
    treeService.delete(id) map (_ => Ok)
  }
}
