package no.nav.klage.kaka.util

import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1
import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2
import no.nav.klage.kaka.domain.kvalitetsvurdering.v3.KvalitetsvurderingV3
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.javaType

@OptIn(ExperimentalStdlibApi::class)
fun setFieldOnObject(obj: Any, fieldToChange: Pair<String, Any?>) {
    val property = obj::class.memberProperties.firstOrNull { it.name == fieldToChange.first } as KMutableProperty<*>

    when (property.returnType.javaType) {
        KvalitetsvurderingV1.RadioValg::class.java -> {
            property.setter.call(obj, KvalitetsvurderingV1.RadioValg.valueOf(fieldToChange.second.toString()))
        }
        KvalitetsvurderingV1.RadioValgRaadgivendeLege::class.java -> {
            property.setter.call(obj, KvalitetsvurderingV1.RadioValgRaadgivendeLege.valueOf(fieldToChange.second.toString()))
        }
        KvalitetsvurderingV2.Radiovalg::class.java -> {
            property.setter.call(obj, KvalitetsvurderingV2.Radiovalg.valueOf(fieldToChange.second.toString()))
        }
        KvalitetsvurderingV2.RadiovalgRaadgivendeLege::class.java -> {
            property.setter.call(obj, KvalitetsvurderingV2.RadiovalgRaadgivendeLege.valueOf(fieldToChange.second.toString()))
        }
        KvalitetsvurderingV3.Radiovalg::class.java -> {
            property.setter.call(obj, KvalitetsvurderingV3.Radiovalg.valueOf(fieldToChange.second.toString()))
        }
        KvalitetsvurderingV3.RadiovalgRaadgivendeLege::class.java -> {
            property.setter.call(obj, KvalitetsvurderingV3.RadiovalgRaadgivendeLege.valueOf(fieldToChange.second.toString()))
        }
        else -> {
            property.setter.call(obj, fieldToChange.second)
        }
    }
}