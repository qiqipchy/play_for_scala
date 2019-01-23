package model

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._

import scala.collection.mutable

case class Product(

                          ean: Long,
                          name: String,
                          description: String)

case class Warehouse(id: Long, name: String)

case class StockItem(
                            id: Long,
                            productId: Long,
                            warehouseId: Long,
                            quantity: Long)

/**
  * Created by wuyuanyuan on 18-12-29.
  */

object Product {

    val products = mutable.HashSet(
        Product(5010255079763L, "Paperclips Large",
            "Large Plain Pack of 1000"),
        Product(5018206244666L, "Giant Paperclips",
            "Giant Plain 51mm 100 pack"),
        Product(5018306332812L, "Paperclip Giant Plain",
            "Giant Plain Pack of 10000"),
        Product(5018306312913L, "No Tear Paper Clip",
            "No Tear Extra Large Pack of 1000"),
        Product(5018206244611L, "Zebra Paperclips",
            "Zebra Length 28mm Assorted 150 Pack")
    )

    def findAll = products.toList.sortBy(_.ean)

    def getAllEan = products.toList.map(_.ean)
    def findByEan(ean: Long) = products.find(_.ean == ean)

    def productForm: Form[Product] = Form(
        mapping("ean" -> longNumber.verifying(findByEan(_).isEmpty),
            "name" -> nonEmptyText,
            "description" -> nonEmptyText
        )(Product.apply)(Product.unapply))

    def add(product: Product) = products += product
}

case class Preparation(orderNumber: String, product: Product, quantity: Int, location: String)

object WareHouse {
    def findAll(): List[String] = {
        List("W23134", "W12342", "W89323", "W32134", "W34143")
    }
}