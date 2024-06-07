package com.ironbird.domain.entity

import java.util.*

class User(
    var id: UUID,
    var lastName: String,
    var firstName: String,
    var biography: String? = null
)
