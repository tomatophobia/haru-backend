package repositories

import scala.concurrent.Future

import models.Tree

/**
  * A pure non-blocking interface for the TreeRepository.
  */
trait TreeRepository {
  def findAll(): Future[List[Tree]]
  def update(update: Tree): Future[Unit]
}

