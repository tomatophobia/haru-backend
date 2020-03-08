package services

import play.api.libs.json.JsValue

import scala.concurrent.Future

import models.Tree

trait TreeService {
  def findAll(): Future[List[Tree]]
}
