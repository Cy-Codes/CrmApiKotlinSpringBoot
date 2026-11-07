package codes.cy.crmapi.api.config

import java.security.SecureRandom
import java.time.Clock
import java.util.random.RandomGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Provides the UTC [Clock] and cryptographically strong [RandomGenerator] used by write operations (audit timestamps and id generation).
 */
@Configuration
class ClockConfig {
    @Bean
    fun utcClock(): Clock = Clock.systemUTC()

    @Bean
    fun randomGenerator(): RandomGenerator = SecureRandom()
}
