package injection

import javax.inject._

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}
import services._
import repositories._

/**
  * Sets up custom components for Play.
  *
  * https://www.playframework.com/documentation/latest/ScalaDependencyInjection
  */
class Module(environment: Environment, configuration: Configuration)
    extends AbstractModule
    with ScalaModule {

  override def configure() = {
    bindDAOs()
    bindServices()
  }

  private def bindDAOs(): Unit = {
    bind[TreeRepository].to[TreeRepositoryImpl]
  }

  def bindServices(): Unit = {
    bind[TreeService].to[TreeServiceImpl]
  }
}
