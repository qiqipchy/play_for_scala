package controllers

import java.lang.management.ManagementFactory

import javax.inject._
import model.Product
import play.api.{Application, Configuration}
import play.api.i18n._
import play.api.mvc._
import model.Product._
import net.sf.ehcache.hibernate.ccs.EhcacheNonstrictReadWriteCache
import org.apache.commons.lang3.SerializationUtils
import play.api.cache.AsyncCacheApi
import play.api.data.Form
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.cache.CacheApi

import scala.concurrent.Future

@Singleton
case class CurrentApplication @Inject()(application: Application)

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cache: AsyncCacheApi, components: ControllerComponents, configuration: Configuration, client: WSClient)
        extends AbstractController(components) {
    implicit val messagesProvider: MessagesProvider = {
        MessagesImpl(supportedLangs.availables.head, messagesApi)
    }

    implicit def lang = supportedLangs.availables.head

    def Authenticated[A](action: Action[A]): Action[A] = Action(action.parser).async { request =>
        if (System.currentTimeMillis % 2 == 0) action(request)
        else Future.successful(Unauthorized)
    }

    def Cached[A](action: Action[A]) = Action(action.parser).async { request =>
        if (System.currentTimeMillis % 2 == 0) action(request)
        else Future.successful(Ok("Cached"))
    }

    //    pr
    def list =
        Authenticated {
            Cached {
                Action {
                    Ok("Ok")
                }
            }
        }

    /**
      * Create an Action to render an HTML page with a welcome message.
      * The configuration in the `routes` file means that this method
      * will be called when the application receives a `GET` request with
      * a path of `/`.
      */
    def index = Action { implicit request =>
        Ok(views.html.index())
    }

    import scala.concurrent.ExecutionContext.Implicits.global

    def product = Action { implicit request =>
        //        Ok
        //        println(getClass.getClassLoader)
        //        println((new SerializationUtils).getClass.getClassLoader)
        //        println(ManagementFactory.getRuntimeMXBean.getName)
        //        Thread.sleep(10000)
        //        Future.never.map(_ => Ok)
        println(Json.toJson(model.Product.getAllEan).toString())
        Ok(Json.toJson(model.Product.getAllEan))
        //        Ok(views.html.list(model.Product.findAll)).withSession("session" -> "session")
    }

    def show(ean: Long) = Action.async { implicit request =>
        cache.get[Product]("product-" + ean).map {
            case Some(product) =>
                println("Cached")
                Ok(views.html.detail(product))
            case None => Product.findByEan(ean).map(p => Ok(views.html.detail(p))).getOrElse(NotFound)
        }

    }

    def addProduct = Action { implicit req =>
        if (req.flash.get("error").isEmpty)
            Ok(views.html.edit(model.Product.productForm))
        else
            Ok(views.html.edit(model.Product.productForm.bind(req.flash.data)))
    }

    def save = Action { implicit req =>
        val form = productForm.bindFromRequest()
        form.fold(
            error => {
                val data = form.data + ("error" -> "validation.errors")
                Redirect(routes.HomeController.addProduct()).flashing(data.toList: _*)
            },
            product => {
                add(product)
                cache.set("product-" + product.ean, product)
                Redirect(routes.HomeController.show(product.ean))
            }
        )
    }

    def slash(slash: String) = Action {
        Ok(slash).flashing("flash" -> "Flash")
    }

    def slash1(slash: String) = Action {
        Ok(slash)
    }

    def test() = Action.async {
        val result = client.url("https://v.qq.com/x/cover/gakoshshys2i1c0/j0027lc9odh.html").get()

        result.map { res =>
            print(res)
            Ok(res.body)
        }
    }

    def insert(product: Product) {
        val insertedProduct = Product.add(product)

        cache.set("product-" + product.ean, product)
    }

    def postProduct2() = Action(parse.tolerantJson) { request =>
        val jsValue = request.body
        jsValue("ds")
        // Do something with the JSON
        Ok
    }

}

object Arere extends App {
    val a = Product.products
    implicit val format = new Format[Product] {
        override def writes(o: Product): JsValue = Json.obj(
            "ean" -> o.ean,
            "name" -> o.name,
            "desc" -> o.description
        )

        override def reads(json: JsValue): JsResult[Product] = {
            val product = Product(json("ean").as[Long], json("name").as[String], json("desc").as[String])
            JsSuccess(product)
        }
    }
    val json = Json.toJson(a).as[List[Product]]
    println(json.toString())
}