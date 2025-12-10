package com.schottenTotten.model;

import java.util.*;


public class Joueur {
    public String name;
    private List<Carte> hand;
    private boolean isAI;

    public Joueur(String name, boolean isAI) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.isAI = isAI;
    }

    public void addCarteToHand(List<Carte> cartes) {
        if(cartes == null || cartes.isEmpty()) {
            throw new IllegalArgumentException("La liste de cartes ne peut pas être nulle ou vide.");
        }
        hand.addAll(cartes);
    }

    public void removeCarteFromHand(Carte carte) {
        if (!hand.remove(carte)) {
            throw new IllegalArgumentException("La carte spécifiée n'est pas dans la main du joueur.");
        }
    }

    public List<Carte> getHand() {
        return hand;
    }

    public String getName() {
        return name;
    }

    public boolean isAI() {
        return isAI;
    }

}