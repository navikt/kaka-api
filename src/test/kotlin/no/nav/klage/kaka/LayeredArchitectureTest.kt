package no.nav.klage.kaka

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures.layeredArchitecture


@AnalyzeClasses(packages = ["no.nav.klage.kaka"], importOptions = [ImportOption.DoNotIncludeTests::class])
class LayeredArchitectureTest {

    fun kakaApiLayeredArchitecture() = layeredArchitecture()
        .layer("Controllers").definedBy("no.nav.klage.kaka.api")
        .layer("View").definedBy("no.nav.klage.kaka.api.view")
        .layer("Services").definedBy("no.nav.klage.kaka.services..")
        .layer("Repositories").definedBy("no.nav.klage.kaka.repositories..")
        .layer("Config").definedBy("no.nav.klage.kaka.config..")
        .layer("Domain").definedBy("no.nav.klage.kaka.domain..")
        .layer("Util").definedBy("no.nav.klage.kaka.util..")
        .layer("Exceptions").definedBy("no.nav.klage.kaka.exceptions..")

    @ArchTest
    val layer_dependencies_are_respected_for_controllers: ArchRule = kakaApiLayeredArchitecture()
        .whereLayer("Controllers").mayOnlyBeAccessedByLayers("Config")

    @ArchTest
    val layer_dependencies_are_respected_for_view: ArchRule = kakaApiLayeredArchitecture()
        .whereLayer("View").mayOnlyBeAccessedByLayers("Controllers", "Services", "Config")

    @ArchTest
    val layer_dependencies_are_respected_for_services: ArchRule = kakaApiLayeredArchitecture()
        .whereLayer("Services").mayOnlyBeAccessedByLayers("Controllers", "Config")

    @ArchTest
    val layer_dependencies_are_respected_for_persistence: ArchRule = kakaApiLayeredArchitecture()
        .whereLayer("Repositories")
        .mayOnlyBeAccessedByLayers("Services", "Controllers", "Config")

}