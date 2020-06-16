package injection

import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}
import services._
import repositories._

case class TestModule() extends ScalaModule {

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
