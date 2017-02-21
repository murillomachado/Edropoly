/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static Modelo.MatrizTransf.TipoTransf.*;
import static Modelo.MatrizTransf.TipoRot.*;
import java.awt.Color;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Luigi
 */
public class Poliedro {
    
    public enum TipoModif {
        BEND, BEVEL, TWIST;
    }
    
    public Ponto centroide;
    private final ArrayList<Ponto> vertices;
    private final ArrayList<Aresta> arestas;
    private final ArrayList<Face> faces;
    private final ArrayList<Face> camadas;
    
    private final int NUM_FACES_ALTURA = 10;
    
    public Poliedro(int numLados, double tamLado, double altura) {
        
        assert(numLados >= 3 && numLados <= 20) : "Tentando criar Poliedro de " + numLados + " lados";
        assert(altura > 0) : "Tentando criar Poliedro com altura " + altura;
        
        centroide = new Ponto(0, 0, 0);
        vertices = new ArrayList<>();
        arestas = new ArrayList<>();
        faces = new ArrayList<>();
        camadas = new ArrayList<>();
        
        for(int i = 0; i < NUM_FACES_ALTURA; i++) {
            camadas.add(new Face());
        }
        
        double anguloInterno = (numLados - 2) * PI / numLados;
        double complementoAngulo = PI - anguloInterno;
        
        Ponto orig[] = new Ponto[NUM_FACES_ALTURA];
        Ponto p0[] = new Ponto[NUM_FACES_ALTURA];
        Ponto p1[] = new Ponto[NUM_FACES_ALTURA];
        
        for(int i = 0; i < numLados; i++) {
            p1[0] = new Ponto(tamLado, 0, 0);
            p1[0].set(cos(i * complementoAngulo) * p1[0].x, sin(i * complementoAngulo) * p1[0].x, 0);
            vertices.add(p1[0]);
            camadas.get(0).vert.add(p1[0]);
            
            for(int j = 1; j < NUM_FACES_ALTURA; j++) {
                p1[j] = new Ponto(p1[j-1]);
                p1[j].z += altura / NUM_FACES_ALTURA;
                vertices.add(p1[j]);
                camadas.get(j).vert.add(p1[j]);
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
        
        // garantir que faces estão ordenadas em sentido anti-horário
        faces.add(new Face());
        for(int i = camadas.get(0).vert.size()-1; i >= 0; i--) {
            faces.get(0).vert.add(camadas.get(0).vert.get(i));
        }
        faces.add(camadas.get(NUM_FACES_ALTURA-1));
        
        for(int i = 1; i < NUM_FACES_ALTURA; i++) {
            for(int j = 0; j < numLados; j++) {
                Face f = new Face();
                int prox = (j+1) % numLados;
                
                f.vert.add(camadas.get(i-1).vert.get(j));
                f.vert.add(camadas.get(i-1).vert.get(prox));
                f.vert.add(camadas.get(i).vert.get(prox));
                f.vert.add(camadas.get(i).vert.get(j));
                
                faces.add(f);
            }
        }
    }
    
    public Poliedro(String stringSave) {
        
        vertices = new ArrayList<>();
        arestas = new ArrayList<>();
        faces = new ArrayList<>();
        camadas = new ArrayList<>();
        
        int c = 0;
        
        String[] s = stringSave.split(" ");
        
        centroide = new Ponto(s[c++]);

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
                f.vert.add(vertices.get(ind));
            }
            faces.add(f);
        }
        
        int numCamadas = Integer.parseInt(s[c++]);
        
        for(int i = 0; i < numCamadas; i++) {
            int numVert = Integer.parseInt(s[c++]);
            Face f = new Face();
            
            for(int j = 0; j < numVert; j++) {
                int ind = Integer.parseInt(s[c++]);
                f.vert.add(vertices.get(ind));
            }
            camadas.add(f);
        }
    }
    
    private Ponto coordenadasPonto(Ponto p) {
        return new Ponto(p.x + centroide.x,
                         p.y + centroide.y,
                         p.z + centroide.z);
    }
    
    public Ponto getVerticeAresta(int ind_aresta, int ponto) {
        
        Ponto r;
        
        if(ponto == 0) {
            r = arestas.get(ind_aresta).v1;
        } else {
            r = arestas.get(ind_aresta).v2;
        }
        
        return coordenadasPonto(r);
    }
    
    public Face getFace(int ind) {
        Face f = new Face();
        
        for(Ponto p : faces.get(ind).vert) {
            f.vert.add(coordenadasPonto(p));
        }
        
        return f;
    }
    
    public int numArestas() {
        return arestas.size();
    }
    
    public int numFaces() {
        return faces.size();
    }
    
    public void mult(MatrizTransf m) {
        for(Face f : camadas) {
            f.mult(m);
        }
    }
    
    public void transladar(double x, double y, double z) {
        centroide.x += x;
        centroide.y += y;
        centroide.z += z;
    }
    
    public void modificar(TipoModif t, double val) {
        
        MatrizTransf m = new MatrizTransf();
        m.setIdentidade();
        MatrizTransf it = null;
        
        if(null != t) switch (t) {
            case BEND:  it = new MatrizTransf(ROTACAO, val / NUM_FACES_ALTURA, Y); break;
            case TWIST: it = new MatrizTransf(ROTACAO, val, Z); break;
            case BEVEL: it = new MatrizTransf(ESCALA, val / NUM_FACES_ALTURA, val / NUM_FACES_ALTURA, 1); break;
        }
        
        for(Face f : camadas) {
            f.mult(m);
            m = m.mult(it);
        }
    }
    
    public String stringSalvar() {
        
        String ret = "";
        
        ret += centroide.stringSalvar() + " ";
        
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
            ret += f.vert.size() + " ";
            for(Ponto p : f.vert) {
                ret += pontos.get(p) + " ";
            }
        }
        
        ret += camadas.size() + " ";
        for(Face f : camadas) {
            ret += f.vert.size() + " ";
            for(Ponto p : f.vert) {
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
        public ArrayList<Ponto> vert;
        
        public Face() {
            vert = new ArrayList<>();
        }
        
        public void mult(MatrizTransf m) {
        
            for(Ponto p : vert) {

                double novo_x = m.m[0][0] * p.x + m.m[0][1] * p.y + m.m[0][2] * p.z + m.m[0][3];
                double novo_y = m.m[1][0] * p.x + m.m[1][1] * p.y + m.m[1][2] * p.z + m.m[1][3];
                double novo_z = m.m[2][0] * p.x + m.m[2][1] * p.y + m.m[2][2] * p.z + m.m[2][3];

                p.set(novo_x, novo_y, novo_z);
            }
        }
        
        public Vetor normal() {
            // dá pra substituir deixando pré-calculado
            Vetor A = new Vetor(vert.get(0)).subtrair(new Vetor(vert.get(1)));
            Vetor B = new Vetor(vert.get(2)).subtrair(new Vetor(vert.get(1)));
            return B.cross(A);
        }
        
        public Ponto getCentroGeometrico() {
            // dá pra substituir deixando pré-calculado
        
            double xmin = vert.get(0).x;
            double xmax = xmin;
            double ymin = vert.get(0).y;
            double ymax = ymin;

            for(Ponto p : vert) {
                xmin = min(xmin, p.x);
                xmax = max(xmax, p.x);

                ymin = min(ymin, p.y);
                ymax = max(ymax, p.y);
            }
        
            // -1 na coordenada z pois esse é o CG considerando pontos em 2D
            return new Ponto((xmin + xmax) / 2, (ymin + ymax) / 2, -1);
        }
        
        @Override
        public String toString() {
            String ret = "face( ";
            for(Ponto p : vert) {
                ret += p;
            }
            ret += " )";
            return ret;
        }
    }
    
}
