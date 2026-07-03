package com.example.workout.domain.deserializers

import com.example.workout.domain.models.workout.WorkoutType
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class WorkoutTypeDeserializer : JsonDeserializer<WorkoutType?> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): WorkoutType? {
        val status = json?.asInt ?: return null
        return WorkoutType.entries.find { it.value == status }
    }
}
