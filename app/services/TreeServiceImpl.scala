package services

import javax.inject.Inject

import scala.concurrent.Future

import repositories.TreeRepository

import models.Tree

class TreeServiceImpl @Inject()(treeRepository: TreeRepository) extends TreeService {
  override def findAll(): Future[List[Tree]] =
    treeRepository.findAll()

  override def insert(insert: Tree): Future[Unit] = 
    treeRepository.insert(insert)

  override def update(update: Tree): Future[Unit] = 
    treeRepository.update(update)

  override def delete(id: Seq[Int]): Future[Unit] =
    treeRepository.delete(id)
}
