package no.nav.klage.kaka.util

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

fun setFieldOnObject(obj: Any, fieldToChange: Pair<String, Any?>) {
    val property = obj::class.memberProperties.firstOrNull { it.name == fieldToChange.first } as KMutableProperty<*>
    property.setter.call(obj, fieldToChange.second)
}