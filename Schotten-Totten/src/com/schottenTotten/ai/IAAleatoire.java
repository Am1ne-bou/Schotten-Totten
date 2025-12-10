package com.schottenTotten.ai;
import java.util.Random;

import com.schottenTotten.model.*;

public class IAAleatoire {
    public static int choisirBorne(Plateau plateau) {
        Random rand = new Random();
        int borneIndex;
        do {
            borneIndex = rand.nextInt(9); 
        } while (plateau.getBornes(borneIndex).isFull()); // Réessayer si la borne est pleine
        return borneIndex;
    }
    public static Carte choisirCarte(Joueur joueur) {
        Random rand = new Random();
        int carteIndex = rand.nextInt(joueur.getHand().size());
        return joueur.getHand().get(carteIndex);
    }
    
}