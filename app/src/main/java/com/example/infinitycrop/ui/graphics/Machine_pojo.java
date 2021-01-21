package com.example.infinitycrop.ui.graphics;

import com.google.firebase.Timestamp;

public class Machine_pojo {

    String machineID, Temperatura, Humedad, Luminosidad;
    Timestamp fecha;

    public Machine_pojo() {

    }

    public Machine_pojo(String humedad, String luminosidad, String temperatura, String machineID, Timestamp fecha) {
        Humedad = humedad;
        Luminosidad = luminosidad;
        Temperatura = temperatura;
        this.machineID = machineID;
        this.fecha = fecha;
    }

    public String getHumedad() {
        return Humedad;
    }

    public void setHumedad(String humedad) {
        Humedad = humedad;
    }


    public String getLuminosidad() {
        return Luminosidad;
    }

    public void setLuminosidad(String luminosidad) {
        Luminosidad = luminosidad;
    }

    public String getTemperatura() {
        return Temperatura;
    }

    public void setTemperatura(String temperatura) {
        Temperatura = temperatura;
    }

    public String getMachineID() {
        return machineID;
    }

    public void setMachineID(String machineID) {
        this.machineID = machineID;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }
}
