package com.schottenTotten.model;

public class Carte {

    private Couleur couleur;
    private int valeur;

    public Carte(Couleur couleur, int valeur) { // valeur entre 1 et 9 couleur 
        if (valeur < 1 || valeur > 9) {
            throw new IllegalArgumentException("La valeur de la carte doit être entre 1 et 9.");
        }
        this.couleur = couleur;
        this.valeur = valeur;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public int getValeur() {
        return valeur;
    }

    @Override
    public String toString() {
        return valeur + "-" + couleur;
    }


}