package me.fmtech.apollo.di.component;

import javax.inject.Singleton;

import dagger.Component;
import me.fmtech.apollo.app.App;
import me.fmtech.apollo.di.module.AppModule;
import me.fmtech.apollo.di.module.HttpModule;
import me.fmtech.apollo.model.http.RetrofitHelper;

@Singleton
@Component(modules = {AppModule.class, HttpModule.class})
public interface AppComponent {

    App getContext();  // 提供App的Context

    RetrofitHelper retrofitHelper();  //提供http的帮助类

}
