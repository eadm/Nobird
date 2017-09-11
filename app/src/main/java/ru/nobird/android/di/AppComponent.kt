package ru.nobird.android.di

import android.app.Application
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.BindsInstance
import ru.nobird.android.App


@Component(
        modules = arrayOf(
                AndroidInjectionModule::class,
                AppModule::class,
                ActivityBuilder::class
        )
)
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}
