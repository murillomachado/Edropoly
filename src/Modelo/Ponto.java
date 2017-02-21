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
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ponto other = (Ponto) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
            return false;
        }
        return true;
    }
}
