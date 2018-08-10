package me.fmtech.apollo.di.component;

import android.app.Activity;

import dagger.Component;
import me.fmtech.apollo.di.module.FragmentModule;
import me.fmtech.apollo.di.scope.FragmentScope;
import me.fmtech.apollo.module.control.ControlFragment;
import me.fmtech.apollo.module.task.create.TaskCreateFragment;
import me.fmtech.apollo.module.task.display.TaskDisplayFragment;
import me.fmtech.apollo.module.task.list.TaskListFragment;
import me.fmtech.apollo.module.task.map.MapSetFragment;
import me.fmtech.apollo.module.task.upload.TaskUploadFragment;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {

    Activity getActivity();

    void inject(ControlFragment fragment);

    void inject(TaskCreateFragment fragment);

    void inject(TaskUploadFragment fragment);

    void inject(TaskListFragment fragment);

    void inject(TaskDisplayFragment fragment);

    void inject(MapSetFragment fragment);
}
