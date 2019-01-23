package controllers

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

import javax.inject._
import org.krysalis.barcode4j.impl.upcean.EAN13Bean
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider
import play.api.Application
import play.api.i18n._
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class Barcode @Inject()(components: ControllerComponents, langs: Langs, messagesApi: MessagesApi)
        extends AbstractController(components) {

    Application
    /**
      * Create an Action to render an HTML page with a welcome message.
      * The configuration in the `routes` file means that this method
      * will be called when the application receives a `GET` request with
      * a path of `/`.
      */
    val ImageResolution = 144

    def barcode(ean: Long) = Action {
        val MimeType = "image/png"
        try {
            val imageData = ean13BarCode(ean, MimeType)
            Ok(imageData).as(MimeType)
        }
        catch {
            case e: IllegalArgumentException =>
                BadRequest("Couldn’t generate bar code. Error: " + e.getMessage)
        }
    }

    def ean13BarCode(ean: Long, mimeType: String): Array[Byte] = {

        val output: ByteArrayOutputStream = new ByteArrayOutputStream
        val canvas: BitmapCanvasProvider =
            new BitmapCanvasProvider(output, mimeType, ImageResolution,
                BufferedImage.TYPE_BYTE_BINARY, false, 0)
        val barcode = new EAN13Bean()
        barcode.generateBarcode(canvas, String valueOf ean)
        canvas.finish
        output.toByteArray
    }
}


