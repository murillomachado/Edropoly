/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Luigi
 */
public class Poliedro {
    
    public final ArrayList<Ponto> vertices;
    public final ArrayList<Aresta> arestas;
    
    public Poliedro(int numLados, double tamLado, double altura) {
        
        assert(numLados >= 3 && numLados <= 20) : "Tentando criar Poliedro de " + numLados + " lados";
        assert(altura > 0) : "Tentando criar Poliedro com altura " + altura;
        
        vertices = new ArrayList<>();
        arestas = new ArrayList<>();
        
        double complementoAngulo = PI - anguloInterno(numLados);
        
        Ponto orig_p = null, orig_q = null;
        Ponto p0 = null, q0 = null, p1 = null, q1 = null;
        
        for(int i = 0; i < numLados; i++) {
            p1 = new Ponto(tamLado, 0, 0);
            p1.set(cos(i * complementoAngulo) * p1.x, sin(i * complementoAngulo) * p1.x, 0);
            vertices.add(p1);
            
            q1 = new Ponto(p1);
            q1.z += altura;
            vertices.add(q1);
            
            if(i == 0) {
                orig_p = p1;
                orig_q = q1;
            }
            else {
                arestas.add(new Aresta(p0, p1));
                arestas.add(new Aresta(q0, q1));
                arestas.add(new Aresta(p1, q1));
            }
            
            p0 = p1;
            q0 = q1;
        }
        
        arestas.add(new Aresta(p1, orig_p));
        arestas.add(new Aresta(q1, orig_q));
        arestas.add(new Aresta(orig_q, orig_p));
    }
    
    public Poliedro(String stringSave) {
        
        vertices = new ArrayList<>();
        arestas = new ArrayList<>();
        
        int c = 0;
        
        String[] s = stringSave.split(" ");
        
        int numVertices = Integer.parseInt(s[c++]);
        
        for(int i = 0; i < numVertices; i++) {
            vertices.add(new Ponto(s[c++]));
        }
        
        int numArestas = Integer.parseInt(s[c++]);
        
        for(int i = 0; i < numArestas; i++) {
            int ind1 = Integer.parseInt(s[c++]);
            int ind2 = Integer.parseInt(s[c++]);
            arestas.add(new Aresta(vertices.get(ind1), vertices.get(ind2)));
        }
    }
    
    private double anguloInterno(int numLados) {
        return (numLados - 2) * PI / numLados;
    }
    
    public int numArestas() {
        return arestas.size();
    }
    
    public void mult(MatrizTransf m) {
        
        for(Ponto p : vertices) {
            
            double novo_x = m.m[0][0] * p.x + m.m[0][1] * p.y + m.m[0][2] * p.z + m.m[0][3];
            double novo_y = m.m[1][0] * p.x + m.m[1][1] * p.y + m.m[1][2] * p.z + m.m[1][3];
            double novo_z = m.m[2][0] * p.x + m.m[2][1] * p.y + m.m[2][2] * p.z + m.m[2][3];
            
            p.set(novo_x, novo_y, novo_z);
        }
        
    }
    
    public Ponto getCentroGeometrico() {
        
        // dá pra substituir deixando os pontos extremos pré-calculados
        
        double xmin = vertices.get(0).x;
        double xmax = xmin;
        double ymin = vertices.get(0).y;
        double ymax = ymin;
        double zmin = vertices.get(0).z;
        double zmax = zmin;
        
        for(Ponto p : vertices) {
            
            xmin = min(xmin, p.x);
            xmax = max(xmax, p.x);
            
            ymin = min(ymin, p.y);
            ymax = max(ymax, p.y);
            
            zmin = min(zmin, p.z);
            zmax = max(zmax, p.z);
            
        }
        
        return new Ponto((xmin + xmax) / 2, (ymin + ymax) / 2, (zmin + zmax) / 2);
        
    }
    
    public String stringSalvar() {
        
        String ret = "";
        
        Map<Ponto, Integer> pontos = new HashMap<>();
        
        ret += vertices.size() + " ";
        for(int i = 0; i < vertices.size(); i++) {
            ret += vertices.get(i).stringSalvar() + " ";
            pontos.put(vertices.get(i), i);
        }
        
        ret += arestas.size() + " ";
        for(Aresta a : arestas) {
            ret += pontos.get(a.v1) + " " + pontos.get(a.v2) + " ";
        }
        
        return ret;
    }
    
    public class Aresta {
        public Ponto v1, v2;
        
        public Aresta(Ponto v1, Ponto v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        @Override
        public String toString() {
            return "aresta( " + v1 + ", " + v2 + " )";
        }
    }
    
}
