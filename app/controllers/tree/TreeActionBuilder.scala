package controllers.tree

import javax.inject.Inject

import net.logstash.logback.marker.LogstashMarker
import play.api.{Logger, MarkerContext}
import play.api.http.{FileMimeTypes}
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


case class TreeControllerComponents @Inject()(
    treeResourceHandler: TreeResourceHandler,
    actionBuilder: DefaultActionBuilder,
    parsers: PlayBodyParsers,
    messagesApi: MessagesApi,
    langs: Langs,
    fileMimeTypes: FileMimeTypes,
    executionContext: scala.concurrent.ExecutionContext)
    extends ControllerComponents

class TreeBaseController @Inject()(tcc: TreeControllerComponents) extends BaseController {
  override protected def controllerComponents: ControllerComponents = tcc

  override def Action: DefaultActionBuilder = tcc.actionBuilder

  def treeResourceHandler: TreeResourceHandler = tcc.treeResourceHandler
}
