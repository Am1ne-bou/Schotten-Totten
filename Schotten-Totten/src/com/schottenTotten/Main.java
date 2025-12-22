package com.schottenTotten;

import com.schottenTotten.controller.Jeu;
import com.schottenTotten.controller.JeuFactory;
import com.schottenTotten.controller.VarianteJeu;
import com.schottenTotten.view.ConsoleView;

public class Main {
    
    public static void main(String[] args) {
        ConsoleView view = new ConsoleView();
        
        // Afficher le titre
        view.afficherTitre();
        
        // Choix de la variante
        int choixVariante = view.afficherMenuVariante();
        VarianteJeu variante = (choixVariante == 1) ? VarianteJeu.BASE : VarianteJeu.TACTIQUE;
        view.afficherVarianteChoisie(variante.getNom());
        
        // Configuration du joueur 1
        view.afficherConfigJoueur(1);
        String nomJ1 = view.demanderNomJoueur("Joueur 1");
        boolean isAI1 = (view.demanderTypeJoueur() == 2);
        
        // Configuration du joueur 2
        view.afficherConfigJoueur(2);
        String nomJ2 = view.demanderNomJoueur("Joueur 2");
        boolean isAI2 = (view.demanderTypeJoueur() == 2);
        
        // Afficher le lancement
        view.afficherLancementPartie(nomJ1, nomJ2, variante.getNom());
        
        // Création et lancement du jeu via la Factory
        Jeu jeu = JeuFactory.creerJeu(variante, nomJ1, isAI1, nomJ2, isAI2);
        jeu.Gameloop();
    }
}
