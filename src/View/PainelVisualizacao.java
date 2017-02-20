/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import Modelo.*;
import Modelo.Poliedro.Face;
import Modelo.Ponto.Coordenada;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Admin
 */
public class PainelVisualizacao extends JPanel {
    
    private Cena c;
    private Coordenada C1, C2;
    
    double zBuffer[][];
    Color cores[][];

    public PainelVisualizacao(Cena c, Coordenada C1, Coordenada C2) {
        super();
        setOpaque(false);
        
        this.c = c;
        this.C1 = C1;
        this.C2 = C2;
        
        zBuffer = null;
        cores = null;
    }
    
    public void setCena(Cena c) {
        this.c = c;
    }
    
    private void drawPixel(Graphics g, int x, int y) {
        g.drawLine(x, y, x, y);
    }
    
    private void drawLine(Graphics g, double x1, double y1, double x2, double y2)
    {
        double dist_x = abs(x1-x2);
        double dist_y = abs(y1-y2);
        
        if(dist_x > dist_y) {
            
            double delta = (y2-y1) / dist_x;
            
            if(x1 > x2) {
                y1 = y2;
                delta = -delta;
            }
            
            int ini = (int) min(x1, x2), fim = (int) max(x1, x2);
            
            for(int i = ini; i <= fim; i++) {
                drawPixel(g, i, (int)y1);
                y1 += delta;
            }
            
        }
        else {
            
            double delta = (x2-x1) / dist_y;
            
            if(y1 > y2) {
                x1 = x2;
                delta = -delta;
            }
            
            int ini = (int) min(y1, y2), fim = (int) max(y1, y2);
            
            for(int i = ini; i <= fim; i++) {
                drawPixel(g, (int)x1, i);
                x1 += delta;
            }
        }
    }
    
    void drawPoliLines(Graphics g, Poliedro p) {
        
        int altura = getHeight();
        int largura = getWidth();
        
        for(int i = 0; i < p.numArestas(); i++) {
            drawLine(g, p.getVerticeAresta(i, 0).getCoordenada(C1) + altura/2, p.getVerticeAresta(i, 0).getCoordenada(C2) + largura/2,
                        p.getVerticeAresta(i, 1).getCoordenada(C1) + altura/2, p.getVerticeAresta(i, 1).getCoordenada(C2) + largura/2);
        }
    }
    
    void wireframe(Graphics g) {
        
        g.setColor(Color.BLACK);
        for(int i = 0; i < c.poliedros.size(); i++) {
            if(i != c.selecionado) {
                drawPoliLines(g, c.poliedros.get(i));
            }
        }
        
        if(c.selecionado != -1) {
            g.setColor(Color.RED);
            drawPoliLines(g, c.poliedros.get(c.selecionado));
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        
        super.paintComponents(g);
        
        g.setColor(Color.LIGHT_GRAY);
        drawLine(g, 0, getHeight()/2, getWidth(), getHeight()/2);
        drawLine(g, getWidth()/2, 0, getWidth()/2, getHeight());
        
        boolean wireframe = false;
        
        if(wireframe) {
            wireframe(g);
        } else {
            zBuffer(g);
       }
    }
    
    void zBuffer(Graphics g) {
        
        if(zBuffer == null) {
            zBuffer = new double[getHeight()][getWidth()];
        }
        if(cores == null) {
            cores = new Color[getHeight()][getWidth()];
        }
        
        for(int i = 0; i < getHeight(); i++) {
            for(int j = 0; j < getWidth(); j++) {
                zBuffer[i][j] = Double.MAX_VALUE;
                cores[i][j] = c.corFundo;
            }
        }
        
        for(Poliedro p : c.poliedros) {
            for(int i = 0; i < p.numFaces(); i++) {
                Face f = p.getFace(i);
                
                Ponto cg = f.getCentroGeometrico();
                Vetor N = f.normal();
                Ponto p0 = f.vert.get(0);
                double pa = N.p.x, pb = N.p.y, pc = N.p.z, pd = - (pa * p0.x + pb * p0.y + pc * p0.z);
                
                if(pc < 1e-10) continue;
                
                cg.z = (-pd - pa * cg.x - pb * cg.y) / pc;
                
                Vetor L = new Vetor(c.origemLuz).subtrair(cg);
                L.normalizar();
                N.normalizar();
                double costeta = L.dot(N);
                
                Vetor R = N.mult(2 * costeta).subtrair(L);
                Vetor S = new Vetor(c.observador).subtrair(cg);
                R.normalizar();
                S.normalizar();
                double cosalfa = R.dot(S);
                
                double Ka = 1, Kd = 1, Ks = 1;
                int n_exp = 1;
                double fatt = min(1, 1 / L.tamanho());
                
                double ambiente = Ka;
                double difusa = (costeta > 0 ? Kd * costeta : 0);
                double especular = (costeta > 0 && cosalfa > 0 ? Ks * Math.pow(cosalfa, n_exp) : 0);
                
                double r = c.Ia * ambiente + fatt * c.Il * (difusa + especular);
                float red = (float)r / 3;
                
                zBuffer(g, f, new Color(red, 0, 0));
            }
        }
        
        for(int i = 0; i < getHeight(); i++) {
            for(int j = 0; j < getWidth(); j++) {
                g.setColor(cores[i][j]);
                drawPixel(g, i, j);
            }
        }
    }
    
    /*
    * Implementação da interpolação dos pontos em arestas para polígonos quase-convexos. Um polígono quase-
    * convexo é aqui definido como um polígono em que qualquer scanline em y intercepta o polígono no máximo
    * duas vezes. Como neste trabalho todas as faces são polígonos convexos, então também são quase-convexos.
    * Ademais os pontos da face devem estar ordenados em sentido anti-horário.
    */
    public void zBuffer(Graphics g, Face f, Color cor) {
        
        ArrayList<Ponto> pontos = f.vert;
        if(todosYIguais(pontos)) return;
        
        for(Ponto p : f.vert) {
            p.x += getHeight()/2;
            p.y += getWidth()/2;
        }
        
        int min = 0, max = 0;
        for(int i = 0; i < pontos.size(); i++) {
            if(pontos.get(i).y < pontos.get(min).y) min = i;
            if(pontos.get(i).y > pontos.get(max).y) max = i;
        }

        Vetor n = f.normal();
        Ponto p0 = pontos.get(0);
        double pa = n.p.x, pb = n.p.y, pc = n.p.z, pd = - (pa * p0.x + pb * p0.y + pc * p0.z);

        int prox = proxDiferenteY(proxDiferenteY(max, 1, pontos), -1, pontos);
        int prev = proxDiferenteY(proxDiferenteY(max, -1, pontos), 1, pontos);
        int a = -1, b = -1;
        
        double delta_xa = 0, delta_xb = 0, delta_z = 0, xa = 0, xb = 0;

        for(int y = (int)Math.floor(pontos.get(max).y); y >= (int)Math.ceil(pontos.get(min).y); y--) {

            if(y <= pontos.get(prox).y || a == -1) {
                a = prox;
                prox = proxDiferenteY(a, 1, pontos);
                delta_xa = (pontos.get(prox).x - pontos.get(a).x) / (pontos.get(a).y - pontos.get(prox).y);
                xa = pontos.get(a).x + delta_xa * (pontos.get(a).y - (int)Math.floor(pontos.get(a).y));
            }
            if(y <= pontos.get(prev).y || b == -1) {
                b = prev;
                prev = proxDiferenteY(b, -1, pontos);
                delta_xb = (pontos.get(prev).x - pontos.get(b).x) / (pontos.get(b).y - pontos.get(prev).y);
                xb = pontos.get(b).x + delta_xb * (pontos.get(b).y - (int)Math.floor(pontos.get(b).y));
            }
            
            double z = (-pd - pa * xa - pb * y) / pc + delta_z * (Math.ceil(xa) - xa);
            
            for(int x = (int)Math.ceil(xa); x <= (int)Math.floor(xb); x++) {
                if(z < zBuffer[x][y]) {
                    zBuffer[x][y] = z;
                    cores[x][y] = cor;
                }
                z += delta_z;
            }

            xa += delta_xa;
            xb += delta_xb;
        }
    }
    
    int proxDiferenteY(int i, int dif, ArrayList<Ponto> pontos) {
        int r = (i + dif + pontos.size()) % pontos.size();

        while(Math.abs(pontos.get(i).y - pontos.get(r).y) < 1e-10) {
            r = (r + dif + pontos.size()) % pontos.size();
        }

        return r;
    }
    
    boolean todosYIguais(ArrayList<Ponto> pontos) {
        for(int i = 1; i < pontos.size(); i++) {
            if(Math.abs(pontos.get(i).y - pontos.get(i-1).y) > 1e-10)
                return false;
        }
        return true;
    }
}
