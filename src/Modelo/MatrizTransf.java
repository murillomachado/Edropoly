/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import static java.lang.Math.*;

/**
 *
 * @author Luigi
 */
public class MatrizTransf {
    
    public enum TipoTransf {
        TRANSLACAO, ROTACAO, ESCALA;
    }
    public enum TipoRot {
        X, Y, Z;
    }
    
    // tamanho matriz = dimensão + 1
    // se alterar dimensão tem que alterar as funções também (não só esta variável)
    private final int TAM = 4;
    public double m[][];
    
    public MatrizTransf() {
        m = new double[TAM][TAM];
    }

    public MatrizTransf(TipoTransf t, double radianos, TipoRot eixo) {
        m = new double[TAM][TAM];
        
        assert(t == TipoTransf.ROTACAO) : "Tentando criar matriz de " + t + " com 2 args";
        
        if(t == TipoTransf.ROTACAO) {
            setMatrizRotacao(radianos, eixo);
        }
    }
    
    public MatrizTransf(TipoTransf t, double x, double y, double z) {
        m = new double[TAM][TAM];
        
        assert(t == TipoTransf.TRANSLACAO || t == TipoTransf.ESCALA) : "Tentando criar matriz de " + t + " com 3 args";
        
        if(t == TipoTransf.TRANSLACAO)  setMatrizTranslacao(x, y, z);
        else if(t == TipoTransf.ESCALA) setMatrizEscala(x, y, z);
    }
    
    private void setIdentidade() {
        for(int i = 0; i < TAM; i++) {
            for(int j = 0; j < TAM; j++) {
                m[i][j] = 0;
            }
        }
        
        for(int i = 0; i < TAM; i++) {
            m[i][i] = 1;
        }
    }
    
    private void setMatrizRotacao(double radianos, TipoRot rot) {
        
        setIdentidade();
        
        double cosseno = cos(radianos);
        double seno = sin(radianos);
        
        if(rot == TipoRot.X) {
            m[1][1] = m[2][2] = cosseno;
            m[2][1] = seno;
            m[1][2] = -seno;
        }
        else if(rot == TipoRot.Y) {
            m[0][0] = m[2][2] = cosseno;
            m[0][2] = seno;
            m[2][0] = -seno;
        }
        else if(rot == TipoRot.Z) {
            m[0][0] = m[1][1] = cosseno;
            m[1][0] = seno;
            m[0][1] = -seno;
        }
    }
    
    private void setMatrizEscala(double escala_x, double escala_y, double escala_z) {
        
        setIdentidade();
        m[0][0] = escala_x;
        m[1][1] = escala_y;
        m[2][2] = escala_z;
    }
    
    private void setMatrizTranslacao(double transl_x, double transl_y, double transl_z) {
        
        setIdentidade();
        m[0][3] = transl_x;
        m[1][3] = transl_y;
        m[2][3] = transl_z;
        
    }
    
    public MatrizTransf mult(MatrizTransf m2) {
        
        MatrizTransf ret = new MatrizTransf();
        
        for(int i = 0; i < TAM; i++) {
            for(int k = 0; k < TAM; k++) {
                for(int j = 0; j < TAM; j++) {
                    ret.m[i][j] += this.m[i][k] * m2.m[k][j];
                }
            }
        }
        
        return ret;
    }
    
}
