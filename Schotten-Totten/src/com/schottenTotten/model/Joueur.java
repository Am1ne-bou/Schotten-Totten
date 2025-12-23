package com.schottenTotten.model;

import java.util.*;

public class Joueur {
    private String name;
    private List<Carte> hand;
    private boolean isAI;
    private int cartesTactiquesJouees;

    public Joueur(String name, boolean isAI) {
        this.name=name;
        this.hand=new ArrayList<>();
        this.isAI=isAI;
        this.cartesTactiquesJouees=0;
    }

    public String getName() { return name; }
    public List<Carte> getHand() { return hand; }
    public boolean isAI() { return isAI; }
    public int getCartesTactiquesJouees() { return cartesTactiquesJouees; }
    public int getHandSize() { return hand.size(); }

    public void addCarteToHand(List<Carte> cartes) {
        if (cartes!=null && !cartes.isEmpty()) hand.addAll(cartes);
    }

    public void addCarteToHand(Carte carte) {
        if (carte!=null) hand.add(carte);
    }

    public void removeCarteFromHand(Carte carte) { hand.remove(carte); }
    public void incrementerCartesTactiquesJouees() { cartesTactiquesJouees++; }

    public List<Carte> getCartesClan() {
        List<Carte> cartesClan=new ArrayList<>();
        for (Carte carte : hand) {
            if (!(carte instanceof CarteTactique)) cartesClan.add(carte);
        }
        return cartesClan;
    }

    public List<CarteTactique> getCartesTactiques() {
        List<CarteTactique> cartesTactiques=new ArrayList<>();
        for (Carte carte : hand) {
            if (carte instanceof CarteTactique) cartesTactiques.add((CarteTactique) carte);
        }
        return cartesTactiques;
    }

    public boolean hasCarteTactique() { return !getCartesTactiques().isEmpty(); }
}