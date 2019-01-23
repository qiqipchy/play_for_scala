import com.google.inject.AbstractModule
import java.time.Clock

import org.scalatestplus.play.PlaySpec
import services.{ApplicationTimer, AtomicCounter, Counter}

/**
  * This class is a Guice module that tells Guice how to bind several
  * different types. This Guice module is created when the Play
  * application starts.
  *
  * Play will automatically use any class called `Module` that is in
  * the root package. You can create modules in other locations by
  * adding `play.modules.enabled` settings to the `application.conf`
  * configuration file.
  */
class Test extends PlaySpec {

    val baseUrl = "http://robot.pt.ai.xiaomi.com/debug/answer/web"
    val newAssistant = "2882303761517406062"
    val params = List(
        "question" -> "%E6%92%AD%E6%94%BE%E9%9F%B3%E4%B9%90%E6%94%B6%E8%97%8F",
        "ev" -> "preview",
        "group" -> "local",
        "version" -> "2.2",
        "session" -> "9fa8c1dea8b14c628ebb4a5695ccaf08",
        "appid" -> newAssistant,
        "payloadMode" -> "none",
        "skip_action" -> "none",
        "user" -> "robot_aaa9Te2cRDSS0ccYTTpHw",
        "uid" -> "",
        "build" -> "",
        "api_version" -> "",
        "fuzzy_search" -> "undefined",
        "domain_url" -> "{%22override_domains%22:[{%22mydomain%22:{%22parser%22:%22c3-ai-dev-i1-01.bj:xxxxx/api2%22,%22provider%22:%22c3-ai-dev-i1-01.bj:xxxxx/api3%22}}]}"
    )
    wsUrl(baseUrl).addQueryStringParameters(params: _*).get().map {
        rep =>
            println(rep)
    }
}
