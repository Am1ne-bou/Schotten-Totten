package com.schottenTotten.model;

import java.util.*;

public class Borne {

    public List<Carte> cartesJ1;
    public List<Carte> cartesJ2;
    public boolean locked;
    public int lastPlayer;   // 0 = aucun, 1 = joueur 1, 2 = joueur 2
    public int proprietaire; // 0 = aucun, 1 = joueur 1, 2 = joueur 2

    public Borne() {
        this.cartesJ1 = new ArrayList<>(3);
        this.cartesJ2 = new ArrayList<>(3);
        this.locked = false;
        this.lastPlayer = 0;
        this.proprietaire = 0;
    }

    public List<Carte> getCartesJ1() {
        return cartesJ1;
    }

    public List<Carte> getCartesJ2() {
        return cartesJ2;
    }
    public boolean isLocked() {
        return locked;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getProprietaire() {
        return proprietaire;
    }
    public void setProprietaire(int proprietaire) {
        this.proprietaire = proprietaire;
    }

    public void addCarte(int joueur, Carte carte) {
        if (joueur == 1) {
            if (cartesJ1.size() < 3) {
                cartesJ1.add(carte);
            } else {
                throw new IllegalStateException("Le joueur 1 ne peut pas ajouter plus de cartes à cette borne.");
            }
        } else if (joueur == 2) {
            if (cartesJ2.size() < 3) {
                cartesJ2.add(carte);
            } else {
                throw new IllegalStateException("Le joueur 2 ne peut pas ajouter plus de cartes à cette borne.");
            }
        } else {
            throw new IllegalArgumentException("Numéro de joueur invalide.");
        }
        lastPlayer = joueur;        
    }

    public int getLastPlayer() {
        return lastPlayer;
    }   


}