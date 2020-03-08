package services

import javax.inject.Inject

import scala.concurrent.Future

import repositories.TreeRepository

import models.Tree

class TreeServiceImpl @Inject()(treeRepository: TreeRepository) extends TreeService {
  override def findAll(): Future[List[Tree]] =
    treeRepository.findAll()
}
