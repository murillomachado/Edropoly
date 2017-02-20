/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luigi
 */
public class Cena {
    
    public final ArrayList<Poliedro> poliedros;
    public int selecionado;
    
    public double Ia, Il;
    public Ponto origemLuz, observador;
    public Color corFundo;
    
    public Cena() {
        poliedros = new ArrayList<>();
        selecionado = -1;
        
        Ia = Il = 1;
        origemLuz = new Ponto(0, 0, 100);
        observador = new Ponto(0, 0, 50);
        corFundo = Color.LIGHT_GRAY;
    }
    
    public Cena(BufferedReader in) {
        
        poliedros = new ArrayList<>();
        selecionado = -1;
        
        try {
            int numPoliedros = Integer.parseInt(in.readLine());
            
            for(int i = 0; i < numPoliedros; i++ ){
                String line = in.readLine();
                poliedros.add(new Poliedro(line));
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Cena.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String stringSalvar() {
        
        String ret = "";
        
        ret += poliedros.size() + "\n";
        
        for(Poliedro p : poliedros) {
            ret += p.stringSalvar() + "\n";
        }
        
        return ret;
    }
    
    public Poliedro getSelecionado() {
        if(selecionado == -1) {
            return null;
        }
        return poliedros.get(selecionado);
    }
    
    public void excluirSelecionado() {
        if(selecionado != -1) {
            poliedros.remove(selecionado);
            selecionado = -1;
        }
    }
    
    public void salvar(String filename) {
        try (PrintWriter saida = new PrintWriter(new FileWriter(filename))) {
            saida.print(stringSalvar());
        } catch (IOException ex) {
            Logger.getLogger(Cena.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static BufferedReader ler(String filename) {
        try {
            BufferedReader entrada = new BufferedReader(new FileReader(filename));
            return entrada;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Cena.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
