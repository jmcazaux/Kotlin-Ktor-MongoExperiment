package com.ironbird.infrastructure.data

import com.ironbird.infrastructure.data.repository.AuthorRepositoryImpl
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.config.tryGetString
import org.bson.UuidRepresentation

const val DATABASE_NAME = "ironbird_library"

private const val DEFAULT_MAX_POOL_SIZE = 20

/**
 * Establishes connection with a MongoDB database.
 *
 * The following configuration properties (in application.yaml/application.conf) can be specified:
 * * `db.mongo.user` username for your database
 * * `db.mongo.password` password for the user
 * * `db.mongo.host` host that will be used for the database connection
 * * `db.mongo.port` port that will be used for the database connection
 * * `db.mongo.maxPoolSize` maximum number of connections to a MongoDB server
 * * `db.mongo.database.name` name of the database
 *
 * IMPORTANT NOTE: in order to make MongoDB connection working, you have to start a MongoDB server first.
 * See the instructions here: https://www.mongodb.com/docs/manual/administration/install-community/
 * all the paramaters above
 *
 * @returns [MongoDatabase] instance
 * */
fun Application.connectToMongoDB(): MongoDatabase {
    val user = environment.config.tryGetString("db.mongo.user")
    val password = environment.config.tryGetString("db.mongo.password")
    val host = environment.config.tryGetString("db.mongo.host") ?: "127.0.0.1"
    val port = environment.config.tryGetString("db.mongo.port") ?: "27017"
    val maxPoolSize = environment.config.tryGetString("db.mongo.maxPoolSize")?.toInt() ?: DEFAULT_MAX_POOL_SIZE
    val databaseName = environment.config.tryGetString("db.mongo.database.name") ?: DATABASE_NAME

    val credentials = user?.let { userVal -> password?.let { passwordVal -> "$userVal:$passwordVal@" } }.orEmpty()
    val uri = "mongodb://$credentials$host:$port/?maxPoolSize=$maxPoolSize&w=majority"

    val (mongoClient, database) = createMongoClientAndDatabase(uri, databaseName)

    environment.monitor.subscribe(ApplicationStopped) {
        mongoClient.close()
    }

    return database
}

internal fun createMongoClientAndDatabase(
    uri: String,
    databaseName: String
): Pair<MongoClient, MongoDatabase> {
    val mongoClient = MongoClient.create(
        MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(uri))
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .build()
    )

    val database = mongoClient.getDatabase(databaseName)
    return Pair(mongoClient, database)
}

fun Application.getAuthorRepository(): AuthorRepositoryImpl {
    val database = connectToMongoDB()
    return AuthorRepositoryImpl(database)
}
