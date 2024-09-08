package net.mythrowaway.app.domain.trash.usecase

class MaxScheduleException : Exception("Trash list max count exceeded")
class EditUseCaseException(
  override val message: String = "EditUseCase error"
) : Exception()