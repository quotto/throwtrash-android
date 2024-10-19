package net.mythrowaway.app.module.trash.usecase

class MaxScheduleException : Exception("Trash list max count exceeded")
class EditUseCaseException(
  override val message: String = "EditUseCase error"
) : Exception()