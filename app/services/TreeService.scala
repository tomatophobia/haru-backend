package services

import play.api.libs.json.JsValue

import scala.concurrent.Future

import models.Tree
import reactivemongo.api.commands.WriteResult

trait TreeService {
  def findAll: Future[List[Tree]]
  def insert(tree: Tree): Future[WriteResult]
  def update(tree: Tree): Future[Unit]
  def delete(id: Seq[Int]): Future[Unit]
}
