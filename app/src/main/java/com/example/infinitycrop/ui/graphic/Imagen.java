package com.example.infinitycrop.ui.graphic;

import java.util.Date;

public class Imagen {
    String titulo;
    String url;
    long tiempo;
    public Imagen() {
    }
    public Imagen(String titulo, String url) {
        this.titulo = titulo;
        this.url = url;
        this.tiempo = new Date().getTime();
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setTiempo(long tiempo) {
        this.tiempo = tiempo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getUrl() {
        return url;
    }

    public long getTiempo() {
        return tiempo;
    }
}
