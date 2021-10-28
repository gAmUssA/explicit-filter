package ef

import io.cloudevents.CloudEvent
import io.cloudevents.core.builder.CloudEventBuilder
import io.cloudevents.spring.webflux.CloudEventHttpMessageReader
import io.cloudevents.spring.webflux.CloudEventHttpMessageWriter
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.codec.CodecCustomizer
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.CodecConfigurer
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*


@SpringBootApplication
@RestController
class Main {

    val badWords = setOf("foo", "bar")

    @PostMapping("/filter")
    fun filter(@RequestBody maybeEvent: CloudEvent?, serverHttpRequest: ServerHttpRequest): CloudEvent? {
        return maybeEvent?.let { event ->
            event.data?.toBytes()?.let { body ->
                val text = String(body).split(Regex("\\s")).joinToString(" ") { word ->
                    if (badWords.find { it == word.lowercase() } != null) {
                        word.map { "*" }.joinToString("")
                    } else {
                        word
                    }
                }

                CloudEventBuilder.from(event)
                    .withId(UUID.randomUUID().toString())
                    .withSource(serverHttpRequest.uri)
                    .withType("text-filtered")
                    .withData(text.toByteArray())
                    .build()
            }
        }
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