package com.schottenTotten.ai;
import java.util.Random;

import com.schottenTotten.model.*;

public class IAAleatoire {
    public static int choisirBorne(Plateau plateau,int nbr_bornes) {
        Random rand = new Random();
        int borneIndex = rand.nextInt(nbr_bornes); 
        return borneIndex;
    }
    public static Carte choisirCarte(Joueur joueur) {
        Random rand = new Random();
        int carteIndex = rand.nextInt(joueur.getHand().size());
        return joueur.getHand().get(carteIndex);
    }
    
}