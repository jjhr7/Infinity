package com.example.infinitycrop.ui.list_machine;

public class MachineModel {

    private String name;
    private long priority;
    private String description;
    private MachineModel(){}

    public MachineModel(String name, long priority, String description) {
        this.name = name;
        this.priority = priority;
        this.description=description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }
}