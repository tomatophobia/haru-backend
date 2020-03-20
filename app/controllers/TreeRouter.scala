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

  override def routes: Routes = {
    case GET(p"/") =>
      controller.findAll

    case PUT(p"/") =>
      controller.update()

    case DELETE(p"/$id") =>
      controller.delete(id.split("").toList.map(_.toInt))

   // case POST(p"/") =>
   //   controller.process

   // case GET(p"/$id") =>
   //   controller.show(id)
  }

}
