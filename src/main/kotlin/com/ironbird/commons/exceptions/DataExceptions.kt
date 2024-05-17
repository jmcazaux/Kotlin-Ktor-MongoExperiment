package com.ironbird.commons.exceptions

class DuplicateEntityException(message: String? = null, cause: Throwable? = null): Exception(message, cause)

class WriteDataException(message: String? = null, cause: Throwable? = null): Exception(message, cause)
