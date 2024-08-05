package net.mythrowaway.app.adapter.di

import dagger.Subcomponent
import net.mythrowaway.app.view.edit.EditActivity
import net.mythrowaway.app.view.edit.EditComposeActivity
import net.mythrowaway.app.view.edit.EditInputEvweekFragment
import net.mythrowaway.app.view.edit.EditMainFragment
import net.mythrowaway.app.view.edit.ExcludeDayFragment
import net.mythrowaway.app.view.edit.IntervalWeeklyScheduleInputFragment
import net.mythrowaway.app.view.edit.MonthlyScheduleInputFragment
import net.mythrowaway.app.view.edit.NeoEditActivity
import net.mythrowaway.app.view.edit.OrdinalWeeklyScheduleInputFragment
import net.mythrowaway.app.view.edit.ScheduleFragment
import net.mythrowaway.app.view.edit.WeeklyScheduleInputFragment

@ActivityScope
@Subcomponent(modules = [EditModule::class])
interface EditComponent {
    @Subcomponent.Factory
    interface Factory{
        fun create():EditComponent
    }

    fun inject(activity: EditActivity)
    fun inject(fragment: EditMainFragment)
    fun inject(activity: NeoEditActivity)
    fun inject(fragment: ScheduleFragment)
    fun inject(fragment: WeeklyScheduleInputFragment)
    fun inject(fragment: MonthlyScheduleInputFragment)
    fun inject(fragment: OrdinalWeeklyScheduleInputFragment)
    fun inject(fragment: IntervalWeeklyScheduleInputFragment)

    fun inject(fragment: ExcludeDayFragment)
    fun inject(activity: EditComposeActivity)
}