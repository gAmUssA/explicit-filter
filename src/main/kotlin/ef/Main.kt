package ef

import io.cloudevents.spring.http.CloudEventHttpUtils
import io.cloudevents.spring.webflux.CloudEventHttpMessageReader
import io.cloudevents.spring.webflux.CloudEventHttpMessageWriter
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.codec.CodecCustomizer
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.CodecConfigurer
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import java.util.*


data class Text(val body: String)

@SpringBootApplication
@RestController
class Main {

    val badWords = setOf("foo", "bar")

    @PostMapping("/filter")
    fun filter(@RequestBody text: Text?, @RequestHeader headers: HttpHeaders?, serverHttpRequest: ServerHttpRequest): ResponseEntity<Text?>? {

        val attributes = CloudEventHttpUtils.fromHttp(headers)
            .withId(UUID.randomUUID().toString())
            .withSource(serverHttpRequest.uri)
            .withType("text-filtered")
            .build()

        val outgoing = CloudEventHttpUtils.toHttp(attributes)

        return text?.body?.split(Regex("\\s"))?.map { word ->
            if (badWords.find { it == word.lowercase() } != null) {
                word.map { "*" }.joinToString("")
            }
            else {
                word
            }
        }?.let { filtered ->
            ResponseEntity.ok().headers(outgoing).body<Text>(text.copy(body = filtered.joinToString(" ")))
        } ?: ResponseEntity.internalServerError().headers(outgoing).build()
    }
}

@Configuration
class CloudEventHandlerConfiguration : CodecCustomizer {
    override fun customize(configurer: CodecConfigurer?) {
        configurer?.customCodecs()?.register(CloudEventHttpMessageReader())
        configurer?.customCodecs()?.register(CloudEventHttpMessageWriter())
    }
}

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}