package net.mythrowaway.app.view

/*
Activity間でのリクエストに利用されるコードを管理する
 */
class ActivityCode {
    companion object {
        const val CALENDAR_REQUEST_UPDATE = 0
        const val CALENDAR_REQUEST_CREATE_FRAGMENT = 1
        const val EXCLUDE_RESULT_DATE_SET = 1
    }
}