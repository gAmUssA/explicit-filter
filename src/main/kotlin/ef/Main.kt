package ef

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

data class Text(val body: String)

@SpringBootApplication
class Main {

    val badWords = setOf("foo", "bar")

    @Bean
    fun filter(): (Text) -> Text = { text ->
        val filtered = text.body.split(Regex("\\s")).map { word ->
            if (badWords.find { it == word.lowercase() } != null) {
                word.map { "*" }.joinToString("")
            }
            else {
                word
            }
        }

        text.copy(body = filtered.joinToString(" "))
    }
}

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}