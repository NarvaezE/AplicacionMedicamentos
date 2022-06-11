package com.example.aplicacionmedicamentos;

public class Medicina {
    private String nombre;
    private int id;
    private int cantidad;
    private int tipoCatalogo;

    public Medicina() {
    }



    public Medicina( int id,String nombre, int cantidad, int tipoCatalogo) {
        this.id = id;
        this.nombre = nombre;

        this.cantidad = cantidad;
        this.tipoCatalogo = tipoCatalogo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setTipoCatalogo(int tipoCatalogo) {
        this.tipoCatalogo = tipoCatalogo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getId() {
        return id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getTipoCatalogo() {
        return tipoCatalogo;
    }

    @Override
    public String toString() {
        return nombre;
    }

}
