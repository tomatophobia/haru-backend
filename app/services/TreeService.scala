package services

import play.api.libs.json.JsValue

import scala.concurrent.Future

import models.Tree

trait TreeService {
  def findAll(): Future[List[Tree]]
  def insert(insert: Tree): Future[Unit]
  def update(update: Tree): Future[Unit]
  def delete(id: Seq[Int]): Future[Unit]
}
