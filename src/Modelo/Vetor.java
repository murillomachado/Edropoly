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
public class Vetor {
    
    public Ponto p;

    public Vetor(double x, double y, double z) {
        p = new Ponto(x, y, z);
    }
    
    public Vetor(Ponto p) {
        this.p = p;
    }
    
    public Vetor somar(Vetor a) {
        return new Vetor(p.x + a.p.x, p.y + a.p.y, p.z + a.p.z);
    }
    
    public Vetor subtrair(Vetor a) {
        return this.somar(a.negativo());
    }
    
    public Vetor subtrair(Ponto p) {
        return subtrair(new Vetor(p));
    }
    
    public Vetor mult(double e) {
        return new Vetor(e * p.x, e * p.y, e * p.z);
    }
    
    public Vetor negativo() {
        return new Vetor(-p.x, -p.y, -p.z);
    }

    public double dot(Vetor a) {
        return p.x * a.p.x + p.y * a.p.y + p.z * a.p.z;
    }

    public Vetor cross(Vetor a) {
        return new Vetor(p.y * a.p.z - p.z * a.p.y,
                         p.z * a.p.x - p.x * a.p.z,
                         p.x * a.p.y - p.y * a.p.x);
    }

    public double tamanho() {
        return Math.sqrt(dot(this));
    }

    public void normalizar() {
        double t = tamanho();

        if(t > 0) {
            p.x /= t;
            p.y /= t;
            p.z /= t;
        }
    }
    
    @Override
    public String toString() {
        return "vetor: " + p;
    }
}
