package com.schottenTotten.model;

import com.schottenTotten.model.carte.Carte;
import com.schottenTotten.model.carte.CarteClan;
import com.schottenTotten.model.carte.CarteTactique;

import java.util.*;

public class Joueur {
    private final String name;
    private List<Carte> hand;
    private final boolean isAI;
    private int cartesTactiquesJouees;

    public Joueur(String name, boolean isAI) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.isAI = isAI;
        this.cartesTactiquesJouees = 0;
    }

    public String getName() {
        return name;
    }

    public List<Carte> getHand() {
        return hand;
    }

    public boolean isAI() {
        return isAI;
    }

    public int getCartesTactiquesJouees() {
        return cartesTactiquesJouees;
    }

    public int getHandSize() {
        return hand.size();
    }

    public void addCartesToHand(List<Carte> carte) {
        if (carte != null && !carte.isEmpty()) hand.addAll(carte);
    }

    public void removeCarteFromHand(Carte carte) {
        hand.remove(carte);
    }

    public void incrementerCartesTactiquesJouees() {
        cartesTactiquesJouees++;
    }

    public List<CarteTactique> getCartesTactiques() {
        List<CarteTactique> cartesTactiques = new ArrayList<>();
        for (Carte carte : hand) {
            if (carte.isTactique()) {
                cartesTactiques.add((CarteTactique) carte);
            }
        }
        return cartesTactiques;
    }

    public List<CarteClan> getCartesClan() {
        List<CarteClan> cartesClan = new ArrayList<>();
        for (Carte carte : hand) {
            if (!carte.isTactique()) {
                cartesClan.add((CarteClan) carte);
            }
        }
        return cartesClan;
    }

    public boolean hasCarteTactique() {
        return !getCartesTactiques().isEmpty();
    }
}