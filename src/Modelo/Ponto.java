/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;


/**
 *
 * @author Luigi
 */
public class Ponto {
    
    public enum Coordenada {
        X, Y, Z;
    }
    
    public double x, y, z;
    
    public Ponto(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Ponto(Ponto p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }
    
    public Ponto(String stringSave) {
        String s[] = stringSave.split(";");
        
        this.x = Double.parseDouble(s[0]);
        this.y = Double.parseDouble(s[1]);
        this.z = Double.parseDouble(s[2]);
    }
    
    public void set(double x, double y, double z) {
        this.x = round(x);
        this.y = round(y);
        this.z = round(z);
    }
    
    public double getCoordenada(Coordenada c) {
        switch(c) {
            case X: return x;
            case Y: return y;
            case Z: return z;
        }
        return -1;
    }
    
    private double round(double x) {
        return x;
        //return Math.round(x * 1000.0) / 1000.0;
    }
    
    public String stringSalvar() {
        return x + ";" + y + ";" + z;
    }

    @Override
    public String toString() {
        return "ponto(" + x + ", " + y + ", " + z + ")";
    }
    
}
