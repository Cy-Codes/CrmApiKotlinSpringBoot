package codes.cy.crmapi.api.config

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverters
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Configures JetBrains `kotlinx.serialization` as the primary JSON serializer for Spring Web MVC.
 */
@Configuration
class KotlinSerializationConfig : WebMvcConfigurer {
    @Bean
    @OptIn(ExperimentalSerializationApi::class)
    fun kotlinxJson(): Json =
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            explicitNulls = true
        }

    override fun configureMessageConverters(builder: HttpMessageConverters.ServerBuilder) {
        val converter = KotlinSerializationJsonHttpMessageConverter(kotlinxJson())
        builder.withKotlinSerializationJsonConverter(converter)
    }
}
