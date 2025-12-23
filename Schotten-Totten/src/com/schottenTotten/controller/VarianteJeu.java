package com.schottenTotten.controller;

public class VarianteJeu {
    public static final VarianteJeu BASE=new VarianteJeu("Variante de Base",6,false,9,5,3);
    public static final VarianteJeu TACTIQUE=new VarianteJeu("Variante Tactique",6,true,9,5,3);
    public static final VarianteJeu EXPRESS=new VarianteJeu("Variante Express",5,false,5,3,2);

    private String nom;
    private int cartesInitiales;
    private boolean cartesTactiques;
    private int nombreBornes;
    private int bornesToWin;
    private int suiteBornes;

    VarianteJeu(String nom, int cartesInitiales, boolean cartesTactiques, int nombreBornes, int bornesToWin, int suiteBornes) {
        this.nom=nom;
        this.cartesInitiales=cartesInitiales;
        this.cartesTactiques=cartesTactiques;
        this.nombreBornes=nombreBornes;
        this.bornesToWin=bornesToWin;
        this.suiteBornes=suiteBornes;
    }

    public String getNom() { return nom; }
    public int getCartesInitiales() { return cartesInitiales; }
    public boolean hasCartesTactiques() { return cartesTactiques; }
    public int getNombreBornes() { return nombreBornes; }
    public int getBornesToWin() { return bornesToWin; }
    public int getSuiteBornes() { return suiteBornes; }

    @Override
    public String toString() { return nom; }
}