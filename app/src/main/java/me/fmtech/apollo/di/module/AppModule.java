package me.fmtech.apollo.di.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.fmtech.apollo.app.App;
import me.fmtech.apollo.model.http.HttpHelper;
import me.fmtech.apollo.model.http.RetrofitHelper;

@Module
public class AppModule {
    private final App application;

    public AppModule(App application) {
        this.application = application;
    }

    @Provides
    @Singleton
    App provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    HttpHelper provideHttpHelper(RetrofitHelper retrofitHelper) {
        return retrofitHelper;
    }

}
