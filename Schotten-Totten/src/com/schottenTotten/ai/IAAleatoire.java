package com.schottenTotten.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.schottenTotten.controller.Jeu;
import com.schottenTotten.model.*;

public class IAAleatoire {
    private static Random rand=new Random();

    public static int choisirBorne(Jeu jeu) {
        int nombreBornes=jeu.getVariante().getNombreBornes();
        int joueur=jeu.getJoueurCourant();

        // Collecter les bornes valides puis en choisir une au hasard
        List<Integer> bornesValides=new ArrayList<>();
        for (int i=0; i<nombreBornes; i++) {
            if (jeu.peutAjouterCarte(jeu.getPlateau().getBorne(i),joueur)) {
                bornesValides.add(i);
            }
        }
        return bornesValides.isEmpty() ? 0 : bornesValides.get(rand.nextInt(bornesValides.size()));
    }

    public static Carte choisirCarte(Joueur joueur, List<Carte> cartesClan) {
        if (cartesClan.isEmpty()) return null;
        return cartesClan.get(rand.nextInt(cartesClan.size()));
    }
}