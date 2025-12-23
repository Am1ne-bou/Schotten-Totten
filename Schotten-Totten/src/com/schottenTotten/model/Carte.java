package com.schottenTotten.model;

public class Carte {
    private Couleur couleur;
    private int valeur;

    public Carte(Couleur couleur, int valeur) {
        this.couleur=couleur;
        this.valeur=valeur;
    }

    public Couleur getCouleur() { return couleur; }
    public int getValeur() { return valeur; }

    @Override
    public String toString() {
        return couleur==null ? "[Tactique]" : valeur+"-"+couleur.name().substring(0,1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj) return true;
        if (obj==null || getClass()!=obj.getClass()) return false;
        Carte carte=(Carte) obj;
        return valeur==carte.valeur && couleur==carte.couleur;
    }

    @Override
    public int hashCode() {
        return 31*valeur+(couleur!=null ? couleur.hashCode() : 0);
    }
}