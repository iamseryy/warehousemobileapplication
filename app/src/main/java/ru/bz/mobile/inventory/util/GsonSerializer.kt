package ru.bz.mobile.inventory.util

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class GsonSerializer {

    data class Box<T>(private val t: T?) {
        fun unbox(): T? = t
    }
    companion object {
        inline fun <reified T> serializeObject(obj: T): String {
            try {
                val gson = GsonBuilder().setPrettyPrinting().create()
                val type: Type = object : TypeToken<Box<T?>?>() {}.type
                return gson.toJson(Box(obj), type)
            } catch (e: Exception) {
                throw GsonSerializerException(
                    message = e.message ?: ""

                )
            }
        }

        inline fun <reified T> deserializeObject(json: String): T? {
            try {
                val gson = GsonBuilder().setPrettyPrinting().create()
                val type: Type = object : TypeToken<Box<T?>?>() {}.type
                val box: Box<T> = gson.fromJson(json, type)
                return box.unbox()
            } catch (e: Exception) {
                throw GsonSerializerException(
                    message = e.message ?: ""
                )
            }
        }
    }

    class GsonSerializerException(message: String) :
        Exception(message)
}