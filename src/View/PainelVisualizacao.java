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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class PainelVisualizacao extends JPanel {
    
    private Cena c;
    private Coordenada C1, C2, C3;
    
    double zBuffer[][];
    Color cores[][];
    
    TIPO_VISUALIZACAO tipo;
    
    public enum TIPO_VISUALIZACAO {
        Wireframe, WireFrameOcultacao, FlatShading, Gouraud, Phong;
    }

    public PainelVisualizacao(Cena c, Coordenada C1, Coordenada C2, Coordenada C3) {
        super();
        setOpaque(false);
        
        this.c = c;
        this.C1 = C1;
        this.C2 = C2;
        this.C3 = C3;
        
        zBuffer = null;
        cores = null;
    }
    
    public void setCena(Cena c) {
        this.c = c;
    }
    
    public void setVisualizacao(TIPO_VISUALIZACAO t) {
        tipo = t;
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
    
    void drawPoliLines(Graphics g, Poliedro p, boolean ocultarFaces) {
        
        int altura = getHeight();
        int largura = getWidth();
        
        // este seria o correto, mas como esta parte não ficou completa para funcionar nos planos ortonormais segue abaixo
        Vetor N = new Vetor(c.VRP).subtrair(c.P);
        
        if(C1 == Coordenada.X && C2 == Coordenada.Y)        N = new Vetor(0, 0, 1);
        else if(C1 == Coordenada.Z && C2 == Coordenada.X)   N = new Vetor(0, 1, 0);
        else if(C1 == Coordenada.Z && C2 == Coordenada.Y)   N = new Vetor(1, 0, 0);
        
        for(int i = 0; i < p.numFaces(); i++) {
            Face f = p.getFace(i);
            if(!ocultarFaces || f.visivel(N)) {
                for(int j = 0; j < f.vert.size(); j++) {
                    int prox = (j+1) % f.vert.size();
                    drawLine(g, f.vert.get(j).getCoordenada(C1) + altura/2, f.vert.get(j).getCoordenada(C2) + largura/2,
                                f.vert.get(prox).getCoordenada(C1) + altura/2,   f.vert.get(prox).getCoordenada(C2) + largura/2);
                }
            }
        }
    }
    
    void wireframe(Graphics g) {
        
        g.setColor(Color.BLACK);
        for(int i = 0; i < c.poliedros.size(); i++) {
            if(i != c.selecionado) {
                drawPoliLines(g, c.poliedros.get(i), tipo == TIPO_VISUALIZACAO.WireFrameOcultacao);
            }
        }
        
        if(c.selecionado != -1) {
            g.setColor(Color.RED);
            drawPoliLines(g, c.poliedros.get(c.selecionado), tipo == TIPO_VISUALIZACAO.WireFrameOcultacao);
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        
        super.paintComponents(g);
        
        g.setColor(Color.LIGHT_GRAY);
        drawLine(g, 0, getHeight()/2, getWidth(), getHeight()/2);
        drawLine(g, getWidth()/2, 0, getWidth()/2, getHeight());
        
        boolean wireframe = (tipo == TIPO_VISUALIZACAO.Wireframe || tipo == TIPO_VISUALIZACAO.WireFrameOcultacao);
        
        if(wireframe) {
            wireframe(g);
        } else {
            zBuffer(g);
            
            if(c.selecionado != -1) {
                g.setColor(Color.LIGHT_GRAY);
                drawPoliLines(g, c.poliedros.get(c.selecionado), true);
            }
       }
    }
    
    float calcularIntensidadeCor(Ponto K, double fatt, double costeta, double cosalfa, double cosalfaPot) {
        
        float ambiente = (float)K.x;
        float difusa = (float)(costeta > 0 ? K.y * costeta : 0);
        float especular = (float)(costeta > 0 && cosalfa > 0 ? K.z * cosalfaPot : 0);
        
        return (float)(ambiente * c.Ia + fatt * c.Il * (difusa + especular));
    }
    
    Color calcularCor(Poliedro poli, Ponto p, Vetor N, Vetor L, double NdotL, Vetor R, Vetor S) {
        
        double costeta = NdotL;
        double cosalfa = R.dot(S);
        double cosalfaPot = Math.pow(cosalfa, poli.n_exp);
        
        double fatt = min(1, 1 / L.tamanho());
        
        float red = calcularIntensidadeCor(poli.KR, fatt, costeta, cosalfa, cosalfaPot);
        float green = calcularIntensidadeCor(poli.KG, fatt, costeta, cosalfa, cosalfaPot);
        float blue = calcularIntensidadeCor(poli.KB, fatt, costeta, cosalfa, cosalfaPot);
        
        final float MAX_INTENSIDADE = 3.0f;
        
        float r = red / MAX_INTENSIDADE;
        float g = green / MAX_INTENSIDADE;
        float b = blue / MAX_INTENSIDADE;
        
        return new Color(r, g, b);
    }
    
    Color calcularCor(Poliedro poli, Ponto p, Vetor N) {
        
        Vetor L = new Vetor(c.origemLuz).subtrair(p);
        N.normalizar();
        L.normalizar();
        double costeta = L.dot(N);
        
        Vetor R = N.mult(2 * costeta).subtrair(L);
        Vetor S = new Vetor(c.VRP).subtrair(p);
        R.normalizar();
        S.normalizar();
        
        return calcularCor(poli, p, N, L, costeta, R, S);
    }
    
    void zBuffer(Graphics g) {
        
        if(zBuffer == null) {
            zBuffer = new double[2 * getHeight()][2 * getWidth()];
        }
        if(cores == null) {
            cores = new Color[2 * getHeight()][2 * getWidth()];
        }
        
        for(int i = 0; i < 2 * getHeight(); i++) {
            for(int j = 0; j < 2 * getWidth(); j++) {
                zBuffer[i][j] = Double.MAX_VALUE;
                cores[i][j] = c.corFundo;
            }
        }
        
        // este seria o correto, mas como esta parte não ficou completa para funcionar nos planos ortonormais segue abaixo
        Vetor N = new Vetor(c.VRP).subtrair(c.P);
        
        if(C1 == Coordenada.X && C2 == Coordenada.Y)        N = new Vetor(0, 0, 1);
        else if(C1 == Coordenada.Z && C2 == Coordenada.X)   N = new Vetor(0, 1, 0);
        else if(C1 == Coordenada.Z && C2 == Coordenada.Y)   N = new Vetor(1, 0, 0);
        
        ArrayList<Vetor> vetoresNormaisMedios = null;
        ArrayList<Color> corVertices = null;
        Map<Ponto, Integer> indVert = null;
        
        for(Poliedro p : c.poliedros) {
            
            if(tipo == TIPO_VISUALIZACAO.Gouraud || tipo == TIPO_VISUALIZACAO.Phong) {
                
                indVert = new HashMap<>();
                for(int i = 0; i < p.numVertices(); i++) {
                    indVert.put(p.getVertice(i), i);
                }
                
                vetoresNormaisMedios = p.getVetoresNormaisMedios();
                
                if(tipo == TIPO_VISUALIZACAO.Gouraud) {
                    corVertices = new ArrayList<>();
                    for(int i = 0; i < p.numVertices(); i++) {
                        corVertices.add(calcularCor(p, p.getVertice(i), vetoresNormaisMedios.get(i)));
                    }
                }
            }
            
            for(int i = 0; i < p.numFaces(); i++) {
                
                Face f = p.getFace(i);
                
                if(!f.visivel(N)) {
                    continue;
                }
                
                if(null != tipo) switch (tipo) {
                    case FlatShading:
                        Ponto cg = f.getCentroGeometrico();
                        Vetor Nf = f.normal();
                        Ponto p0 = f.vert.get(0);
                        double pa = Nf.p.x, pb = Nf.p.y, pc = Nf.p.z, pd = - (pa * p0.x + pb * p0.y + pc * p0.z);
                        if(pc < 1e-10) continue;
                        cg.z = (-pd - pa * cg.x - pb * cg.y) / pc;
                        zBuffer(g, p, f, tipo, calcularCor(p, cg, Nf), null, null);
                        break;
                        
                    case Gouraud:
                        ArrayList<Color> verticeCor = new ArrayList<>();
                        
                        for(Ponto pon : f.vert) {
                            int ind = indVert.get(pon);
                            verticeCor.add(corVertices.get(ind));
                        }
                        
                        zBuffer(g, p, f, tipo, null, verticeCor, null);
                        break;
                        
                    case Phong:
                        ArrayList<Vetor> verticesNormal = new ArrayList<>();
                        
                        for(Ponto pon : f.vert) {
                            int ind = indVert.get(pon);
                            verticesNormal.add(vetoresNormaisMedios.get(ind));
                        }
                        
                        zBuffer(g, p, f, tipo, null, null, verticesNormal);
                        break;
                }
            }
        }
        
        for(int i = 0; i < getHeight(); i++) {
            for(int j = 0; j < getWidth(); j++) {
                
                g.setColor(mediaCor(cores[2*i][2*j],
                                    cores[2*i][2*j+1],
                                    cores[2*i+1][2*j],
                                    cores[2*i+1][2*j+1]));
                drawPixel(g, i, j);
            }
        }
    }
    
    Color mediaCor(Color a, Color b, Color c, Color d) {
        return new Color((a.getRed() + b.getRed() + c.getRed() + d.getRed()) / 4,
                         (a.getGreen()+ b.getGreen() + c.getGreen() + d.getGreen()) / 4,
                         (a.getBlue()+ b.getBlue() + c.getBlue() + d.getBlue()) / 4);
    }
    
    private class ColorFloatingPoint {
        float r, g, b;
        
        ColorFloatingPoint(Color c) {
            r = c.getRed(); g = c.getGreen(); b = c.getBlue();
        }
        ColorFloatingPoint(ColorFloatingPoint c) {
            r = c.r; g = c.g; b = c.b;
        }
        ColorFloatingPoint(ColorFloatingPoint c, float f) {
            r = c.r * f; g = c.g * f; b = c.b * f;
        }
        void somar(ColorFloatingPoint c) {
            r += c.r; g += c.g; b += c.b;
        }
        void subtrair(ColorFloatingPoint c) {
            r -= c.r; g -= c.g; b -= c.b;
        }
        void subtrair(Color c) {
            r -= c.getRed(); g -= c.getGreen(); b -= c.getBlue();
        }
        void mult(float f) {
            r *= f; g *= f; b *= f;
        }
        Color getColor() {
            return new Color(Math.round(r), Math.round(g), Math.round(b));
        }
    }
    
    /*
    * Implementação da interpolação dos pontos em arestas para polígonos quase-convexos. Um polígono quase-
    * convexo é aqui definido como um polígono em que qualquer scanline em y intercepta o polígono no máximo
    * duas vezes. Como neste trabalho todas as faces são polígonos convexos, então também são quase-convexos.
    * Ademais os pontos da face devem estar ordenados em sentido anti-horário.
    */
    public void zBuffer(Graphics g, Poliedro poli, Face f, TIPO_VISUALIZACAO tipo,
            Color corFlat, ArrayList<Color> verticesCorGouread, ArrayList<Vetor> verticesNormalPhong) {
        
        ArrayList<Ponto> pontos = f.vert;
        if(todosYIguais(pontos)) return;
        
        for(Ponto p : f.vert) {
            Ponto aux = new Ponto(p);
            
            p.x = aux.getCoordenada(C1);
            p.y = aux.getCoordenada(C2);
            p.z = aux.getCoordenada(C3);
            
            p.x = 2 * p.x + getHeight();
            p.y = 2 * p.y + getWidth();
        }
        
        /*System.out.println("z buffer face: ");
        for(Ponto p : f.vert) {
            System.out.println(p);
        }*/
        
        int min = 0, max = 0;
        for(int i = 0; i < pontos.size(); i++) {
            if(pontos.get(i).y < pontos.get(min).y) min = i;
            if(pontos.get(i).y > pontos.get(max).y) max = i;
        }
        
        int prox = proxDiferenteY(proxDiferenteY(max, 1, pontos), -1, pontos);
        int prev = proxDiferenteY(proxDiferenteY(max, -1, pontos), 1, pontos);
        int a = -1, b = -1;
        
        double xa = 0, xb = 0, za = 0, zb = 0, delta_xa = 0, delta_xb = 0, delta_za = 0, delta_zb = 0;
        
        // Gouraud
        ColorFloatingPoint c = null, ca = null, cb = null, delta_c = null, delta_ca = null, delta_cb = null;
        
        // Phong
        Vetor v = null, va = null, vb = null, delta_v = null, delta_va = null, delta_vb = null;

        for(int y = (int)Math.floor(pontos.get(max).y); y >= (int)Math.ceil(pontos.get(min).y); y--) {

            if(Math.abs(y - pontos.get(min).y) > 1e-10) {
                while(y <= pontos.get(prox).y || a == -1) {
                    a = prox;
                    prox = proxDiferenteY(a, 1, pontos);
                    delta_xa = (pontos.get(prox).x - pontos.get(a).x) / (pontos.get(a).y - pontos.get(prox).y);
                    delta_za = (pontos.get(prox).z - pontos.get(a).z) / (pontos.get(a).y - pontos.get(prox).y);
                    xa = pontos.get(a).x + delta_xa * (pontos.get(a).y - (int)Math.floor(pontos.get(a).y));
                    za = pontos.get(a).z + delta_za * (pontos.get(a).y - (int)Math.floor(pontos.get(a).y));
                    
                    if(tipo == TIPO_VISUALIZACAO.Gouraud) {
                        delta_ca = new ColorFloatingPoint(verticesCorGouread.get(prox));
                        delta_ca.subtrair(verticesCorGouread.get(a));
                        delta_ca.mult((float)(1 / (pontos.get(a).y - pontos.get(prox).y)));
                        
                        ca = new ColorFloatingPoint(verticesCorGouread.get(a));
                        ca.somar(new ColorFloatingPoint(delta_ca, (float)(pontos.get(a).y - (int)Math.floor(pontos.get(a).y))));
                    }
                    
                    if(tipo == TIPO_VISUALIZACAO.Phong) {
                        delta_va = verticesNormalPhong.get(prox).subtrair(verticesNormalPhong.get(a));
                        delta_va = delta_va.mult(1 / (pontos.get(a).y - pontos.get(prox).y));
                        va = verticesNormalPhong.get(a).somar(delta_va.mult((pontos.get(a).y - (int)Math.floor(pontos.get(a).y))));
                    }
                }
                while(y <= pontos.get(prev).y || b == -1) {
                    b = prev;
                    prev = proxDiferenteY(b, -1, pontos);
                    delta_xb = (pontos.get(prev).x - pontos.get(b).x) / (pontos.get(b).y - pontos.get(prev).y);
                    delta_zb = (pontos.get(prev).z - pontos.get(b).z) / (pontos.get(b).y - pontos.get(prev).y);
                    xb = pontos.get(b).x + delta_xb * (pontos.get(b).y - (int)Math.floor(pontos.get(b).y));
                    zb = pontos.get(b).z + delta_zb * (pontos.get(b).y - (int)Math.floor(pontos.get(b).y));
                    
                    if(tipo == TIPO_VISUALIZACAO.Gouraud) {
                        delta_cb = new ColorFloatingPoint(verticesCorGouread.get(prev));
                        delta_cb.subtrair(verticesCorGouread.get(b));
                        delta_cb.mult((float)(1 / (pontos.get(b).y - pontos.get(prev).y)));
                        
                        cb = new ColorFloatingPoint(verticesCorGouread.get(b));
                        cb.somar(new ColorFloatingPoint(delta_cb, (float)(pontos.get(b).y - (int)Math.floor(pontos.get(b).y))));
                    }
                    
                    if(tipo == TIPO_VISUALIZACAO.Phong) {
                        delta_vb = verticesNormalPhong.get(prev).subtrair(verticesNormalPhong.get(b));
                        delta_vb = delta_vb.mult(1 / (pontos.get(b).y - pontos.get(prev).y));
                        vb = verticesNormalPhong.get(b).somar(delta_vb.mult((pontos.get(b).y - (int)Math.floor(pontos.get(b).y))));
                    }
                }
            }
            
            double delta_z = (zb - za) / (xb - xa);
            double z = za + delta_z * (Math.ceil(xa) - xa);
            
            if(tipo == TIPO_VISUALIZACAO.Gouraud) {
                delta_c = new ColorFloatingPoint(cb);
                delta_c.subtrair(ca);
                delta_c.mult((float)(1 / (xb - xa)));
                
                c = new ColorFloatingPoint(ca);
                c.somar(new ColorFloatingPoint(delta_c, (float)(Math.ceil(xa) - xa)));
            }
            
            if(tipo == TIPO_VISUALIZACAO.Phong) {
                delta_v = vb.subtrair(va).mult(1 / (xb - xa));
                v = va.somar(delta_v.mult((Math.ceil(xa) - xa)));
            }
            
            //System.out.println("y + " + y + ", a " + a + ", xa " + xa + ", b " + b + ", xb " + xb + ", delta xb " + delta_xb);
            
            for(int x = (int)Math.ceil(xa); x <= (int)Math.floor(xb); x++) {
                
                if(x >= 0 && y >= 0 && x < zBuffer.length && y < zBuffer[0].length) {
                    if(z < zBuffer[x][y]) {
                        zBuffer[x][y] = z;

                        if(tipo == TIPO_VISUALIZACAO.FlatShading) {
                            cores[x][y] = corFlat;
                        } else if(tipo == TIPO_VISUALIZACAO.Gouraud) {
                            cores[x][y] = c.getColor();
                        } else if(tipo == TIPO_VISUALIZACAO.Phong) {
                            cores[x][y] = calcularCor(poli, new Ponto((x - getHeight()) / 2, (y - getWidth()) / 2, z), v);
                        }
                    }
                }
                
                z += delta_z;
                
                if(tipo == TIPO_VISUALIZACAO.Gouraud) {
                    c.somar(delta_c);
                }
                if(tipo == TIPO_VISUALIZACAO.Phong) {
                    v = v.somar(delta_v);
                }
            }

            xa += delta_xa;
            xb += delta_xb;
            
            za += delta_za;
            zb += delta_zb;
            
            if(tipo == TIPO_VISUALIZACAO.Gouraud) {
                ca.somar(delta_ca);
                cb.somar(delta_cb);
            }
            if(tipo == TIPO_VISUALIZACAO.Phong) {
                va = va.somar(delta_va);
                vb = vb.somar(delta_vb);
            }
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
