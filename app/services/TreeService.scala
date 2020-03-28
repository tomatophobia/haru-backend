package services

import play.api.libs.json.JsValue

import scala.concurrent.Future

import models.Tree

trait TreeService {
  def findAll: Future[List[Tree]]
  def insert(tree: Tree): Future[Unit]
  def update(tree: Tree): Future[Unit]
  def delete(id: Seq[Int]): Future[Unit]
}
