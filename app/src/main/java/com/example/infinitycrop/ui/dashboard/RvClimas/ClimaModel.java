package com.example.infinitycrop.ui.dashboard.RvClimas;

public class ClimaModel {

    private String temperatura;
    private String humedad;
    private String luminosidad;
    private String name;
    private String creator;
    private String uid;
    private String machineId;
    private boolean defecto;

    public ClimaModel() {
    }

    public ClimaModel(String temperatura, String humedad, String luminosidad, String name, String creator, String uid, boolean defecto) {
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.luminosidad = luminosidad;
        this.name = name;
        this.creator = creator;
        this.uid = uid;
        this.defecto = defecto;
    }

    public ClimaModel(String temperatura, String humedad, String luminosidad, String name, String creator, String uid, String machineId, boolean defecto) {
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.luminosidad = luminosidad;
        this.name = name;
        this.creator = creator;
        this.uid = uid;
        this.machineId = machineId;
        this.defecto = defecto;
    }

    public String getLuminosidad() {
        return luminosidad;
    }

    public void setLuminosidad(String luminosidad) {
        this.luminosidad = luminosidad;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getHumedad() {
        return humedad;
    }

    public void setHumedad(String humedad) {
        this.humedad = humedad;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public boolean isDefecto() {
        return defecto;
    }

    public void setDefecto(boolean defecto) {
        this.defecto = defecto;
    }
}
