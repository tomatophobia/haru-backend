package services

import javax.inject.Inject

import scala.concurrent.Future

import repositories.TreeRepository

import models.Tree
import reactivemongo.api.commands.WriteResult


class TreeServiceImpl @Inject()(treeRepository: TreeRepository) extends TreeService {
  override def findAll: Future[List[Tree]] =
    treeRepository.findAll

  override def insert(tree: Tree): Future[WriteResult] = 
    treeRepository.insert(tree)

  override def update(tree: Tree): Future[WriteResult] = 
    treeRepository.update(tree)

  override def delete(id: Seq[Int]): Future[WriteResult] =
    treeRepository.delete(id)
}
