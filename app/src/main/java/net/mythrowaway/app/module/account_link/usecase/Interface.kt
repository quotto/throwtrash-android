package net.mythrowaway.app.module.account_link.usecase

import net.mythrowaway.app.module.account_link.entity.FinishAccountLinkRequestInfo
import net.mythrowaway.app.module.account_link.dto.StartAccountLinkResponse

interface AccountLinkRepositoryInterface {
  fun saveAccountLinkRequestInfo(finishAccountLinkRequestInfo: FinishAccountLinkRequestInfo)
  fun getAccountLinkRequestInfo(): FinishAccountLinkRequestInfo?
}
interface AccountLinkApiInterface {
  fun accountLink(id: String, idToken: String): StartAccountLinkResponse
  fun accountLinkAsWeb(id: String, idToken: String): StartAccountLinkResponse
}
