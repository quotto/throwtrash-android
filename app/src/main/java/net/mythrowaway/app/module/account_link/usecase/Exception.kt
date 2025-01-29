package net.mythrowaway.app.module.account_link.usecase

class UserIdNotFoundException(message: String): Exception(message)
class InvalidRedirectUriException(message: String): Exception(message)