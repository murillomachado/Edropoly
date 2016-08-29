/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luigi
 */
public class Cena {
    
    public final ArrayList<Poliedro> poliedros;
    
    public Cena() {
        poliedros = new ArrayList<>();
    }
    
    public Cena(BufferedReader in) {
        
        poliedros = new ArrayList<>();
        
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
    
    private String stringSalvar() {
        
        String ret = "";
        
        ret += poliedros.size() + "\n";
        
        for(Poliedro p : poliedros) {
            ret += p.stringSalvar() + "\n";
        }
        
        return ret;
    }
    
    public void salvar(String filename) {
        try {
            PrintWriter saida = new PrintWriter(new FileWriter(filename));
            System.out.println(stringSalvar());
            saida.print(stringSalvar());
            saida.close();
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
