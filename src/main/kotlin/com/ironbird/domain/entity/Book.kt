package com.ironbird.domain.entity

import java.util.UUID


data class Book(
    var id: UUID,
    var title: String,
    var author_id: UUID,
) {
    var description: String? = null
}