package com.schottenTotten;

import com.schottenTotten.controller.*;
import com.schottenTotten.view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        ConsoleView view=new ConsoleView();

        view.afficherTitre();

        int choixVariante=view.afficherMenuVariante();
        VarianteJeu variante;
        switch (choixVariante) {
            case 2: variante=VarianteJeu.TACTIQUE; break;
            case 3: variante=VarianteJeu.EXPRESS; break;
            default: variante=VarianteJeu.BASE;
        }
        view.afficherVarianteChoisie(variante.getNom());

        view.afficherConfigJoueur(1);
        String nomJ1=view.demanderNomJoueur("Joueur 1");
        boolean isAI1=(view.demanderTypeJoueur()==2);

        view.afficherConfigJoueur(2);
        String nomJ2=view.demanderNomJoueur("Joueur 2");
        boolean isAI2=(view.demanderTypeJoueur()==2);

        view.afficherLancementPartie(nomJ1,nomJ2,variante.getNom());

        Jeu jeu=JeuFactory.creerJeu(variante,nomJ1,isAI1,nomJ2,isAI2);
        jeu.Gameloop();
    }
}