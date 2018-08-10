package me.fmtech.apollo.di.component;

import android.app.Activity;

import dagger.Component;
import me.fmtech.apollo.di.module.ActivityModule;
import me.fmtech.apollo.di.scope.ActivityScope;
import me.fmtech.apollo.module.main.MainActivity;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    Activity getActivity();

    void inject(MainActivity activity);
}
