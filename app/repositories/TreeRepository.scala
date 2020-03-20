package repositories

import scala.concurrent.Future

import models.Tree

/**
  * A pure non-blocking interface for the TreeRepository.
  */
trait TreeRepository {
  def findAll(): Future[List[Tree]]
  def insert(insert: Tree): Future[Unit]
  def update(update: Tree): Future[Unit]
  def delete(id: Seq[Int]): Future[Unit]
}

