package com.ironbird.plugins

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.types.ObjectId

@Serializable
data class Car(
    val brandName: String,
    val model: String,
    val number: String
) {
    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromDocument(document: Document): Car = json.decodeFromString(document.toJson())
    }
}

class CarService(private val database: MongoDatabase) {
    lateinit var collection: MongoCollection<Document>

    init {
        suspend {
            database.createCollection("cars")
            collection = database.getCollection("cars")
        }
    }

    // Create new car
    suspend fun create(car: Car): String = withContext(Dispatchers.IO) {
        val doc = car.toDocument()
        collection.insertOne(doc)
        doc["_id"].toString()
    }

    // Read a car
    suspend fun read(id: String): Car? = withContext(Dispatchers.IO) {
        collection.find(Filters.eq("_id", ObjectId(id))).first()?.let(Car::fromDocument)
    }

    // Update a car
    suspend fun update(id: String, car: Car): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndReplace(Filters.eq("_id", ObjectId(id)), car.toDocument())
    }

    // Delete a car
    suspend fun delete(id: String): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndDelete(Filters.eq("_id", ObjectId(id)))
    }
}

