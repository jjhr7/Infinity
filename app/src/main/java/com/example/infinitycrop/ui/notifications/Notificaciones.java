package com.example.infinitycrop.ui.notifications;

public class Notificaciones {
    //recycler bueno
    private String nombre;
    private String info;
    private String foto;
    private String fecha;

    public Notificaciones(String nombre, String info, String foto, String fecha) {
        this.nombre = nombre;
        this.info = info;
        this.foto = foto;
        this.fecha = fecha;
    }

    public Notificaciones(){}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
