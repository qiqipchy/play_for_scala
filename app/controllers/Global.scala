package controllers

import java.io.{File, FileWriter}
import java.util.Locale

import akka.actor.{Actor, ActorSystem, Props}
import javax.inject.Inject
import model.WareHouse
import play.api.i18n._
import play.api.inject.ApplicationLifecycle
import play.api.mvc.Flash
import play.twirl.api.Html

import scala.reflect.io.File

/**
  * Created by wuyuanyuan on 19-1-14.
  */
class Global @Inject()(langs: Langs, messagesProvider: MessagesApi, lifecycle: ApplicationLifecycle) {

    import scala.concurrent.duration._
    import scala.concurrent.ExecutionContext.Implicits.global

    val ourSystem = ActorSystem("Pick_System")
    for (warehouse <- WareHouse.findAll()) {
        val actor = ourSystem.actorOf(Props(new PickListActor(warehouse, langs.availables.head, messagesProvider)))
        ourSystem.scheduler.schedule(
            0.seconds, 10.seconds, actor, "send"
        )
    }
    lifecycle.addStopHook {
        () =>
            ourSystem.terminate()
    }
}

class PickListActor(warehouse: String, lang: Lang, messagesApi: MessagesApi) extends Actor {

    implicit val messagesProvider: MessagesProvider = {
        MessagesImpl(lang, messagesApi)
    }

    implicit val flash = Flash(Map())

    override def receive: Receive = {
        case "send" =>
            val pickList = PickList.findByWareHouse(warehouse)
            val timestamp = new java.util.Date

//            val html = views.html.pickList(warehouse, pickList, timestamp)(Lang(Locale.ENGLISH), flash, messagesProvider)
//            sendToUser(html)
        case _ => play.api.Logger.warn("unsupported message type")
    }

    def sendToUser(content: Html) = {
        //        val writer = new FileWriter("temp.txt", true)
        //        try {
        //            writer.write(content.toString())
        //        } finally {
        //            writer.flush()
        //            writer.close()
        //        }

    }
}
