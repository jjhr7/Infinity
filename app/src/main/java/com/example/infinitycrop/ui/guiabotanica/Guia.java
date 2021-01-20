package com.example.infinitycrop.ui.guiabotanica;

public class Guia {
    //eliminar notificaciones sin animación
    String nombrePlanta;
    String descripcionPlanta;
    String humedadPlanta;
    String temperaturaPlanta;
    String luminosidadPlanta;
    String fotoPlanta;
    private String uid;

    public Guia(){}


    public Guia(String nombrePlanta, String fotoPlanta, String descripcionPlanta, String humedadPlanta, String temperaturaPlanta, String luminosidadPlanta) {
        this.nombrePlanta = nombrePlanta;
        this.descripcionPlanta = descripcionPlanta;
        this.humedadPlanta = humedadPlanta;
        this.temperaturaPlanta = temperaturaPlanta;
        this.luminosidadPlanta = luminosidadPlanta;
        this.fotoPlanta = fotoPlanta;
    }

    public String getNombrePlanta() {
        return nombrePlanta;
    }

    public void setNombrePlanta(String nombrePlanta) {
        this.nombrePlanta = nombrePlanta;
    }

    public String getDescripcionPlanta() {
        return descripcionPlanta;
    }

    public void setDescripcionPlanta(String descripcionPlanta) {
        this.descripcionPlanta = descripcionPlanta;
    }

    public String getHumedadPlanta() {
        return humedadPlanta;
    }

    public void setHumedadPlanta(String humedadPlanta) {
        this.humedadPlanta = humedadPlanta;
    }

    public String getTemperaturaPlanta() {
        return temperaturaPlanta;
    }

    public void setTemperaturaPlanta(String temperaturaPlanta) {
        this.temperaturaPlanta = temperaturaPlanta;
    }

    public String getLuminosidadPlanta() {
        return luminosidadPlanta;
    }

    public void setLuminosidadPlanta(String luminosidadPlanta) {
        this.luminosidadPlanta = luminosidadPlanta;
    }

    public String getFotoPlanta() {
        return fotoPlanta;
    }

    public void setFotoPlanta(String fotoPlanta) {
        this.fotoPlanta = fotoPlanta;
    }

}
