/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edropoly;

import Modelo.Cena;
import Modelo.MatrizTransf;
import Modelo.Poliedro;
import Modelo.Ponto;
import View.PainelPrincipal;
import View.PainelVisualizacao;
import java.awt.FlowLayout;
import javax.swing.JFrame;

/**
 *
 * @author Murillo Machado
 */
public class Edropoly {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Cena c = new Cena();
        c.poliedros.add(new Poliedro(6, 50, 50));
        
        c.poliedros.get(0).mult(new MatrizTransf(MatrizTransf.TipoTransf.TRANSLACAO, 150, 150, 150));
        
        JFrame frame = new PainelPrincipal(c);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
}
