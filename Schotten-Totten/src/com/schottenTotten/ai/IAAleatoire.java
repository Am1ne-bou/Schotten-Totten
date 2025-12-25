package com.schottenTotten.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.schottenTotten.controller.Jeu;
import com.schottenTotten.model.*;
import com.schottenTotten.model.carte.CarteClan;
import com.schottenTotten.model.carte.CarteTactique;
import com.schottenTotten.model.enums.Couleur;

public class IAAleatoire {
    private static Random rand=new Random();

    public static int choisirBorne(Jeu jeu) {
        int nombreBornes=jeu.getVariante().getNombreBornes();
        int joueur=jeu.getJoueurCourant();

        List<Integer> bornesValides=new ArrayList<>();
        for (int i=0; i<nombreBornes; i++) {
            if (jeu.peutAjouterCarte(jeu.getPlateau().getBorne(i),joueur)) {
                bornesValides.add(i);
            }
        }
        return bornesValides.isEmpty() ? 0 : bornesValides.get(rand.nextInt(bornesValides.size()));
    }

    public static CarteClan choisirCarte(Joueur joueur, List<CarteClan> cartesClan) {
        if (cartesClan.isEmpty()) return null;
        return cartesClan.get(rand.nextInt(cartesClan.size()));
    }

    // Choisit quel type de pioche (1=clan, 2=tactique)
    public static int choisirTypePioche(boolean clanDispo, boolean tactiqueDispo) {
        if (clanDispo && tactiqueDispo) return rand.nextInt(10)<7 ? 1 : 2;
        return clanDispo ? 1 : 2;
    }

    // Choisit si jouer clan (1) ou tactique (2), préfère clan
    public static int choisirTypeCarteAJouer(boolean hasCartesClan, boolean peutJouerTactique) {
        if (!hasCartesClan) return peutJouerTactique ? 2 : 0;
        if (!peutJouerTactique) return 1;
        // 70% clan, 30% tactique
        return rand.nextInt(10)<7 ? 1 : 2;
    }

    // Choisit une carte tactique au hasard
    public static CarteTactique choisirCarteTactique(List<CarteTactique> cartesTactiques) {
        if (cartesTactiques.isEmpty()) return null;
        return cartesTactiques.get(rand.nextInt(cartesTactiques.size()));
    }

    // Choisit une couleur au hasard
    public static Couleur choisirCouleur() {
        Couleur[] couleurs=Couleur.values();
        return couleurs[rand.nextInt(couleurs.length)];
    }

    // Choisit une valeur au hasard dans l'intervalle
    public static int choisirValeur(int min, int max) {
        return min+rand.nextInt(max-min+1);
    }

    // Choisit une borne non revendiquée au hasard
    public static int choisirBorneNonRevendiquee(Jeu jeu) {
        int nombreBornes=jeu.getVariante().getNombreBornes();
        List<Integer> bornesValides=new ArrayList<>();
        for (int i=0; i<nombreBornes; i++) {
            if (!jeu.getPlateau().getBorne(i).isLocked()) {
                bornesValides.add(i);
            }
        }
        return bornesValides.isEmpty() ? 0 : bornesValides.get(rand.nextInt(bornesValides.size()));
    }

    // Choisit un index au hasard
    public static int choisirIndex(int max) {
        return rand.nextInt(max);
    }
}