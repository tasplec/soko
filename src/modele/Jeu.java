/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;


import java.awt.Point;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;


public class Jeu extends Observable {

    public static final int SIZE_X = 20;
    public static final int SIZE_Y = 10;



    private Heros heros;

    private HashMap<Case, Point> map = new  HashMap<Case, Point>(); // permet de récupérer la position d'une case à partir de sa référence
    private Case[][] grilleEntites = new Case[SIZE_X][SIZE_Y]; // permet de récupérer une case à partir de ses coordonnées


    public HashMap<Case, Point> getMap() {
        return map;
    }

    public Jeu() {
        initialisationNiveau();
    }

    public Case[][] getGrille() {
        return grilleEntites;
    }
    
    public Heros getHeros() {
        return heros;
    }
    public void deplacerHeros(Direction d) {
        heros.avancerDirectionChoisie(d);
        setChanged();
        notifyObservers();
    }

    private String[][] chargerNiveau(int i){
        String[][] tabNiveau = new String[21][11];
        int j =0;
        File file = new File(String.format("Niveau\\%d.txt",i));
        BufferedReader br= null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String st;
        while (true)
        {
            try {
                if (!((st = br.readLine()) != null)) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String[] l = st.split(",");
            for(int n=0;n<l.length;n++)
            {
                tabNiveau[n][j] = l[n];
            }
            j++;
            System.out.println(st);

        }
        return tabNiveau;
    }
    public ArrayList<String> tabCible = new ArrayList<String>();
    public ArrayList<Bloc> tabBloc = new ArrayList<Bloc>();

    private boolean finNiveau()
    {

        return false;
    }
    private void initialisationNiveau() {
        String[][] tabNiveau = chargerNiveau(1);
        for(int i=0;i<20;i++)
        {
            for(int j=0;j<10;j++)
            {
                addCase(new Vide(this), i, j);
                if(tabNiveau[i][j].equals("M"))
                {
                    addCase(new Mur(this), i, j);
                }
                if(tabNiveau[i][j].equals("H"))
                {
                    heros = new Heros(this, grilleEntites[i-1][j]);
                    //System.out.println("COORD BUG: "+i+j+" YA QUOI: "+ grilleEntites[i][j].getClass()); 8 4
                }
                if(tabNiveau[i][j].equals("C"))
                {
                    addCase(new Cible(this), i, j);
                    tabCible.add(String.format("%d,%d",i,j));
                }
                if(tabNiveau[i][j].equals("V"))
                {
                    addCase(new Vide(this), i, j);
                }
                if(tabNiveau[i][j].equals("B"))
                {
                    Bloc b = new Bloc(this,grilleEntites[i][j]);
                    tabBloc.add(b);
                }
            }
        }
        for(int i=0;i< tabBloc.size();i++)
        {
            Point coord = map.get(tabBloc.get(i).getCase());
            System.out.println("X: "+coord.x+" Y:"+coord.y);
        }

        addCase(new Vide(this), 8, 4);
    }

    private void addCase(Case e, int x, int y) {
        grilleEntites[x][y] = e;
        map.put(e, new Point(x, y));
    }
    

    
    /** Si le déplacement de l'entité est autorisé (pas de mur ou autre entité), il est réalisé
     * Sinon, rien n'est fait.
     */
    public boolean deplacerEntite(Entite e, Direction d) {
        boolean retour = true;
        
        Point pCourant = map.get(e.getCase());

        Point pCible = calculerPointCible(pCourant, d);
        notifyObservers();
        if (contenuDansGrille(pCible)) {
            Entite eCible = caseALaPosition(pCible).getEntite();
            if (eCible != null) {
                eCible.pousser(d);
            }

            // si la case est libérée
            if (caseALaPosition(pCible).peutEtreParcouru()) {
                e.getCase().quitterLaCase();
                caseALaPosition(pCible).entrerSurLaCase(e);

            } else {
                retour = false;
            }

        } else {
            retour = false;
        }

        notifyObservers(tabBloc.get(0));
        return retour;
    }
    
    
    private Point calculerPointCible(Point pCourant, Direction d) {
        Point pCible = null;
        
        switch(d) {
            case haut: pCible = new Point(pCourant.x, pCourant.y - 1); break;
            case bas : pCible = new Point(pCourant.x, pCourant.y + 1); break;
            case gauche : pCible = new Point(pCourant.x - 1, pCourant.y); break;
            case droite : pCible = new Point(pCourant.x + 1, pCourant.y); break;     
            
        }
        
        return pCible;
    }
    

    
    /** Indique si p est contenu dans la grille
     */
    private boolean contenuDansGrille(Point p) {
        return p.x >= 0 && p.x < SIZE_X && p.y >= 0 && p.y < SIZE_Y;
    }
    
    private Case caseALaPosition(Point p) {
        Case retour = null;
        
        if (contenuDansGrille(p)) {
            retour = grilleEntites[p.x][p.y];
        }
        
        return retour;
    }

}
