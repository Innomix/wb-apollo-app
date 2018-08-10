package me.fmtech.apollo.model.bean;

import com.google.gson.Gson;

import java.util.List;

public class TaskBean {
    private String name;
    private String audio1;
    private int id;
    private List<LocationBean> milestones;

    public TaskBean(String name, List<LocationBean> milestones) {
        this.name = name;
        this.milestones = milestones;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LocationBean> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<LocationBean> milestones) {
        this.milestones = milestones;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, this.getClass());
    }

    public String getAudio1() {
        return audio1;
    }

    public void setAudio1(String audio1) {
        this.audio1 = audio1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
