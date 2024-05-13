package com.ironbird.domain.entity

import java.util.UUID

class User(
    var id: UUID,
    var lastName: String,
    var firstName: String,
) {
    var biography: String? = null
}