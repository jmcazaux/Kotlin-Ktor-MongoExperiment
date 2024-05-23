package com.ironbird.commons.exceptions

class DuplicateEntityException(message: String? = null, cause: Throwable? = null): Exception(message, cause)

class DataWriteException(message: String? = null, cause: Throwable? = null): Exception(message, cause)
