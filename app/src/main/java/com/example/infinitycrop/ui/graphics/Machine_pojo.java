package com.example.infinitycrop.ui.graphics;

import com.google.firebase.Timestamp;

public class Machine_pojo {

    int Humedad, Humedad_Ambiente, Luminosidad;
    String machineID, Temperatura;
    Timestamp fecha;

    public Machine_pojo() {

    }

    public Machine_pojo(int humedad, int humedad_Ambiente, int luminosidad, String temperatura, String machineID, Timestamp fecha) {
        Humedad = humedad;
        Humedad_Ambiente = humedad_Ambiente;
        Luminosidad = luminosidad;
        Temperatura = temperatura;
        this.machineID = machineID;
        this.fecha = fecha;
    }

    public int getHumedad() {
        return Humedad;
    }

    public void setHumedad(int humedad) {
        Humedad = humedad;
    }

    public int getHumedadA() {
        return Humedad_Ambiente;
    }

    public void setHumedadA(int humedad_Ambiente) {
        Humedad_Ambiente = humedad_Ambiente;
    }

    public int getLuminosidad() {
        return Luminosidad;
    }

    public void setLuminosidad(int luminosidad) {
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
