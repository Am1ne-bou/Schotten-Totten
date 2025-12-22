package com.schottenTotten.controller;

public class VarianteJeu {
    public static final VarianteJeu BASE = new VarianteJeu("Variante de Base", 6, false, 9);
    public static final VarianteJeu TACTIQUE = new VarianteJeu("Variante Tactique", 6, true, 9);

    private String nom;
    private int cartesInitiales;
    private boolean cartesTactiques;
    private int nombreBornes;

    VarianteJeu(String nom, int cartesInitiales, boolean cartesTactiques, int nombreBornes) {
        this.nom = nom;
        this.cartesInitiales = cartesInitiales;
        this.cartesTactiques = cartesTactiques;
        this.nombreBornes = nombreBornes;
    }

    public String getNom() {
        return nom;
    }

    public int getCartesInitiales() {
        return cartesInitiales;
    }

    public boolean hasCartesTactiques() {
        return cartesTactiques;
    }

    public int getNombreBornes() {
        return nombreBornes;
    }

    @Override
    public String toString() {
        return nom;
    }
}
