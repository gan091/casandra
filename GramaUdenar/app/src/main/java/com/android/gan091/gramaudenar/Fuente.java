package com.android.gan091.gramaudenar;

/**
 * Created by SIB02 on 20/11/2017.
 */

public class Fuente {
    float ancho,alto,area;
    int numPiso;

    public Fuente(float ancho){
        this.ancho = ancho;
    }

    public Fuente(float ancho, float alto) {
        this.ancho = ancho;
        this.alto = alto;
    }

    public Fuente(float ancho, float alto, int numPiso) {
        this.ancho = ancho;
        this.alto = alto;
        this.numPiso = numPiso;
    }

    public float getAncho() {
        return ancho;
    }

    public void setAncho(float ancho) {
        this.ancho = ancho;
    }

    public float getAlto() {
        return alto;
    }

    public void setAlto(float alto) {
        this.alto = alto;
    }

    public float getArea() {
        area = alto * ancho;
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public int getNumPiso() {
        return numPiso;
    }

    public void setNumPiso(int numPiso) {
        this.numPiso = numPiso;
    }
}
