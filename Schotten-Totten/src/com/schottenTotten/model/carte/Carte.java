package com.schottenTotten.model.carte;

import com.schottenTotten.model.enums.Couleur;

public abstract class Carte {
    public Couleur couleur;
    public int valeur;

    public Carte(Couleur couleur, int valeur) {
        this.couleur = couleur;
        this.valeur = valeur;
    }

    public Carte() {
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public int getValeur() {
        return valeur;
    }

    public abstract boolean isTactique();

    public boolean isClan() {
        return !isTactique();
    }
}