package net.mythrowaway.app.module.alarm.entity

data class AlarmConfig(
    private val _enabled: Boolean = false,
    private val _hourOfDay: Int = 7,
    private val _minute:Int = 0,
    private val _notifyEveryday: Boolean = false,
    private val _notifyTomorrow: Boolean = false
) {
    val enabled: Boolean
        get() = _enabled

    val hourOfDay: Int
        get() = _hourOfDay

    val minute: Int
        get() = _minute

    val notifyEveryday: Boolean
        get() = _notifyEveryday

    val notifyTomorrow: Boolean
        get() = _notifyTomorrow
}
