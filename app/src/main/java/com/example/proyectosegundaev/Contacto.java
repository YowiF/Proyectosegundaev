package com.example.proyectosegundaev;

public class Contacto {
    public String nombre;
    public String direccion;
    public String email;
    public String telefono;

    public Contacto(String nombre, String direccion, String email,String telefono) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.email = email;
        this.telefono = telefono;
    }

    public Contacto() {

    }

    @Override
    public String toString() {
        return "Contacto{" +
                "nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
