package services

import javax.inject.Inject

import scala.concurrent.Future

import repositories.TreeRepository

import models.Tree


class TreeServiceImpl @Inject()(treeRepository: TreeRepository) extends TreeService {
  override def findAll: Future[List[Tree]] =
    treeRepository.findAll

  override def insert(tree: Tree): Future[Unit] = 
    treeRepository.insert(tree)

  override def update(tree: Tree): Future[Unit] = 
    treeRepository.update(tree)

  override def delete(id: Seq[Int]): Future[Unit] =
    treeRepository.delete(id)
}
