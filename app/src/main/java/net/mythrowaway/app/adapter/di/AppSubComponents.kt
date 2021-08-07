package net.mythrowaway.app.adapter.di

import dagger.Module

@Module(subcomponents = [
    CalendarComponent::class,
    EditComponent::class,
    ScheduleListComponent::class,
    AlarmComponent::class,
    ConnectComponent::class,
    ActivateComponent::class,
    PublishCodeComponent::class])
class AppSubComponents