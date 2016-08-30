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
    public final ArrayList<Face> faces;
    
    public Poliedro(int numLados, double tamLado, double altura) {
        
        assert(numLados >= 3 && numLados <= 20) : "Tentando criar Poliedro de " + numLados + " lados";
        assert(altura > 0) : "Tentando criar Poliedro com altura " + altura;
        
        final int NUM_FACES_ALTURA = 3;
        
        vertices = new ArrayList<>();
        arestas = new ArrayList<>();
        faces = new ArrayList<>();
        
        double complementoAngulo = PI - anguloInterno(numLados);
        
        Ponto orig[] = new Ponto[NUM_FACES_ALTURA];
        Ponto p0[] = new Ponto[NUM_FACES_ALTURA];
        Ponto p1[] = new Ponto[NUM_FACES_ALTURA];
        
        for(int i = 0; i < numLados; i++) {
            p1[0] = new Ponto(tamLado, 0, 0);
            p1[0].set(cos(i * complementoAngulo) * p1[0].x, sin(i * complementoAngulo) * p1[0].x, 0);
            vertices.add(p1[0]);
            
            for(int j = 1; j < NUM_FACES_ALTURA; j++) {
                p1[j] = new Ponto(p1[j-1]);
                p1[j].z += altura / NUM_FACES_ALTURA;
                vertices.add(p1[j]);
            }
            
            for(int k = 1; k < NUM_FACES_ALTURA; k++) {
                arestas.add(new Aresta(p1[k-1], p1[k]));
            }
            
            if(i == 0) {
                System.arraycopy(p1, 0, orig, 0, NUM_FACES_ALTURA);
            }
            else {
                for(int k = 0; k < NUM_FACES_ALTURA; k++) {
                    arestas.add(new Aresta(p0[k], p1[k]));
                }
            }
            
            System.arraycopy(p1, 0, p0, 0, NUM_FACES_ALTURA);
        }
        
        for(int k = 0; k < NUM_FACES_ALTURA; k++) {
            arestas.add(new Aresta(p1[k], orig[k]));
        }
    }
    
    public Poliedro(String stringSave) {
        
        vertices = new ArrayList<>();
        arestas = new ArrayList<>();
        faces = new ArrayList<>();
        
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
        
        int numFaces = Integer.parseInt(s[c++]);
        
        for(int i = 0; i < numFaces; i++) {
            int numVert = Integer.parseInt(s[c++]);
            Face f = new Face();
            
            for(int j = 0; j < numVert; j++) {
                int ind = Integer.parseInt(s[c++]);
                f.vertices.add(vertices.get(ind));
            }
            faces.add(f);
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
        
        ret += faces.size() + " ";
        for(Face f : faces) {
            ret += f.vertices.size() + " ";
            for(Ponto p : f.vertices) {
                ret += pontos.get(p) + " ";
            }
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
    
    public class Face {
        public ArrayList<Ponto> vertices;
        
        public Face() {
            vertices = new ArrayList<>();
        }
        
        @Override
        public String toString() {
            String ret = "face( ";
            for(Ponto p : vertices) {
                ret += p;
            }
            ret += " )";
            return ret;
        }
    }
    
}
