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
import Modelo.Ponto.Coordenada;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 *
 * @author Admin
 */
public class PainelVisualizacao extends JPanel {
    
    private Cena c;
    private Coordenada C1, C2;

    public PainelVisualizacao(Cena c, Coordenada C1, Coordenada C2) {
        super();
        setOpaque(false);
        
        this.c = c;
        this.C1 = C1;
        this.C2 = C2;
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
        
        for(int i = 0; i < p.numArestas(); i++) {
            drawLine(g, p.arestas.get(i).v1.getCoordenada(C1), p.arestas.get(i).v1.getCoordenada(C2),
                        p.arestas.get(i).v2.getCoordenada(C1), p.arestas.get(i).v2.getCoordenada(C2));
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        
        super.paintComponents(g);
        g.setColor(Color.BLACK);
        for(Poliedro p : c.poliedros) {
            drawPoliLines(g, p);
        }
    }
    
}
