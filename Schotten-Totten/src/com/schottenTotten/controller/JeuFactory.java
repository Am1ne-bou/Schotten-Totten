package com.schottenTotten.controller;

import com.schottenTotten.model.Joueur;

public class JeuFactory {
    public static Jeu creerJeu(VarianteJeu variante,Joueur joueur1,Joueur joueur2) {
        if (variante==null) throw new IllegalArgumentException("La variante ne peut pas être null");
        return new Jeu(variante,joueur1,joueur2);
    }
}