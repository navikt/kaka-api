package no.nav.klage.kaka.config


import no.nav.klage.kaka.util.getLogger
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.util.concurrent.TimeUnit
import javax.cache.CacheManager
import javax.cache.configuration.MutableConfiguration
import javax.cache.expiry.CreatedExpiryPolicy
import javax.cache.expiry.Duration

@EnableCaching
@Configuration
class CacheWithJCacheConfiguration(private val environment: Environment) : JCacheManagerCustomizer {

    companion object {

        const val FINISHED_FOR_LEDER_CACHE = "finished_for_leder"
        const val TOTAL_CACHE = "total"
        const val OPEN_CACHE = "open"
        const val VEDTAKSINSTANSLEDER_CACHE = "vedtaksinstansleder"

        val cacheKeys =
            listOf(
                FINISHED_FOR_LEDER_CACHE,
                TOTAL_CACHE,
                OPEN_CACHE,
                VEDTAKSINSTANSLEDER_CACHE,
            )

        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    override fun customize(cacheManager: CacheManager) {
        cacheKeys.forEach { cacheName ->
            cacheManager.createCache(cacheName, cacheConfiguration(standardDuration()))
        }
    }

    private fun cacheConfiguration(duration: Duration) =
        MutableConfiguration<Any, Any>()
            .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(duration))
            .setStoreByValue(false)
            .setStatisticsEnabled(true)

    private fun standardDuration() =
        if (environment.activeProfiles.contains("prod-gcp")) {
            Duration(TimeUnit.HOURS, 6L)
        } else {
            Duration(TimeUnit.MINUTES, 10L)
        }

}