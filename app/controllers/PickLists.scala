package controllers

import javax.inject._
import play.api.{Application, Configuration}
import play.api.i18n._
import play.api.mvc._

object PickList {
    def findByWareHouse(wareHouse: String) = model.Product.findAll.filter(_.ean > wareHouse.length).map {
        model.Preparation(
            wareHouse, _,
            200, if (wareHouse.length % 2 == 0) "New York" else "Chiago")
    }
}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class PickList @Inject()(components: ControllerComponents, langs: Langs, messagesApi: MessagesApi, configuration: Configuration)
        extends AbstractController(components) {
    val availableLangs: Seq[Lang] = langs.availables
    implicit val messagesProvider: MessagesProvider = {
        MessagesImpl(availableLangs.head, messagesApi)
    }
    implicit val lang: Lang = availableLangs.head

    def pickList(warehouse: String) = Action { implicit request =>
        val pickList = PickList.findByWareHouse(warehouse)
        val timestamp = new java.util.Date
        1 / 0
        Ok(views.html.pickList(warehouse, pickList, timestamp))
    }

}

