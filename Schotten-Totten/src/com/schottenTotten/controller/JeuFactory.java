package com.schottenTotten.controller;

public class JeuFactory {
    public static Jeu creerJeu(VarianteJeu variante, String nomJ1, boolean isAI1, String nomJ2, boolean isAI2) {
        if (variante==null) throw new IllegalArgumentException("La variante ne peut pas être null");
        return new Jeu(variante,nomJ1,isAI1,nomJ2,isAI2);
    }
}