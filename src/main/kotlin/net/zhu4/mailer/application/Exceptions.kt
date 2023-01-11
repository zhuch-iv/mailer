package net.zhu4.mailer.application

class EsiServerException(msg: String, cause: Throwable? = null): RuntimeException(msg, cause)

class EsiClientException(msg: String, cause: Throwable? = null): RuntimeException(msg, cause)
