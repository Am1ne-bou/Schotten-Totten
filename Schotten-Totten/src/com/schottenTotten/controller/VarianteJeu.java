package com.schottenTotten.controller;

public enum VarianteJeu {
    BASE("Variante de Base", 6, false, 9, 5, 3),
    TACTIQUE("Variante Tactique", 6, true, 9, 5, 3),
    EXPRESS("Variante Express", 5, false, 5, 3, 2),
    EXPRESS_TACTIQUE("Variante Express Tactique", 5, true, 5, 3, 2);

    private final String nom;
    private final int cartesInitiales;
    private final boolean cartesTactiques;
    private final int nombreBornes;
    private final int bornesToWin;
    private final int suiteBornes;

    VarianteJeu(String nom, int cartesInitiales, boolean cartesTactiques,
                int nombreBornes, int bornesToWin, int suiteBornes) {
        this.nom = nom;
        this.cartesInitiales = cartesInitiales;
        this.cartesTactiques = cartesTactiques;
        this.nombreBornes = nombreBornes;
        this.bornesToWin = bornesToWin;
        this.suiteBornes = suiteBornes;
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

    public int getBornesToWin() {
        return bornesToWin;
    }

    public int getSuiteBornes() {
        return suiteBornes;
    }
}