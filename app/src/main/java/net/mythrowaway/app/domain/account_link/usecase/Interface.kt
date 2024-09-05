package net.mythrowaway.app.domain.account_link.usecase

import net.mythrowaway.app.domain.account_link.entity.FinishAccountLinkRequestInfo
import net.mythrowaway.app.domain.account_link.dto.StartAccountLinkResponse

interface AccountLinkRepositoryInterface {
  fun saveAccountLinkRequestInfo(finishAccountLinkRequestInfo: FinishAccountLinkRequestInfo)
  fun getAccountLinkRequestInfo(): FinishAccountLinkRequestInfo
}
interface AccountLinkApiInterface {
  fun accountLink(id: String): StartAccountLinkResponse
  fun accountLinkAsWeb(id: String): StartAccountLinkResponse
}
