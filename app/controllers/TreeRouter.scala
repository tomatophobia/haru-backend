package controllers

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
  * Routes and URLs to the TreeResource controller.
  */
class TreeRouter @Inject()(controller: TreeController) extends SimpleRouter {
  val prefix = "/trees"

//  def link(id: Seq[Int]): String = {
//    import io.lemonlabs.uri.dsl._
//    val url = prefix / id.map(_.toString).foldRight("")(_ + _)
//    url.toString()
//  }

  override def routes: Routes = {
    case GET(p"/") =>
      controller.findAll

   // case POST(p"/") =>
   //   controller.process

   // case GET(p"/$id") =>
   //   controller.show(id)
  }

}
