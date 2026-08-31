package no.nav.klage.kaka

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures.layeredArchitecture

@AnalyzeClasses(packages = ["no.nav.klage.kaka"], importOptions = [ImportOption.DoNotIncludeTests::class])
class LayeredArchitectureTest {
    private fun kakaApiLayeredArchitecture() =
        layeredArchitecture()
            .consideringAllDependencies()
            .layer("Controllers")
            .definedBy("no.nav.klage.kaka.api")
            .layer("View")
            .definedBy("no.nav.klage.kaka.api.view")
            .layer("Services")
            .definedBy("no.nav.klage.kaka.services..")
            .layer("Repositories")
            .definedBy("no.nav.klage.kaka.repositories..")
            .layer("Config")
            .definedBy("no.nav.klage.kaka.config..")
            .layer("Domain")
            .definedBy("no.nav.klage.kaka.domain..")
            .layer("Util")
            .definedBy("no.nav.klage.kaka.util..")
            .layer("Exceptions")
            .definedBy("no.nav.klage.kaka.exceptions..")

    @ArchTest
    val layerDependenciesAreRespectedForControllers: ArchRule =
        kakaApiLayeredArchitecture()
            .whereLayer("Controllers")
            .mayOnlyBeAccessedByLayers("Config")

    @ArchTest
    val layerDependenciesAreRespectedForView: ArchRule =
        kakaApiLayeredArchitecture()
            .whereLayer("View")
            .mayOnlyBeAccessedByLayers("Controllers", "Services", "Config")

    @ArchTest
    val layerDependenciesAreRespectedForServices: ArchRule =
        kakaApiLayeredArchitecture()
            .whereLayer("Services")
            .mayOnlyBeAccessedByLayers("Controllers", "Config")

    @ArchTest
    val layerDependenciesAreRespectedForPersistence: ArchRule =
        kakaApiLayeredArchitecture()
            .whereLayer("Repositories")
            .mayOnlyBeAccessedByLayers("Services", "Controllers", "Config")
}
