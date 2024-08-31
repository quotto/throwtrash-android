package net.mythrowaway.app.application.di

import dagger.Module

@Module(subcomponents = [
    CalendarComponent::class,
    EditComponent::class,
    AlarmComponent::class,
    ConnectComponent::class,
    InformationComponent::class]
)
class AppSubComponents