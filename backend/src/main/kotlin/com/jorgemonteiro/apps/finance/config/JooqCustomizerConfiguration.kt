package com.jorgemonteiro.apps.finance.config

import org.jooq.conf.RenderNameCase
import org.springframework.boot.jooq.autoconfigure.DefaultConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Customizes JOOQ to render names in lowercase,
 * matching PostgreSQL conventions (H2-based codegen generates uppercase).
 */
@Configuration
class JooqCustomizerConfiguration {

    @Bean
    fun jooqCustomizer(): DefaultConfigurationCustomizer {
        return DefaultConfigurationCustomizer { configuration ->
            configuration.settings()
                .withRenderNameCase(RenderNameCase.LOWER)
        }
    }
}
