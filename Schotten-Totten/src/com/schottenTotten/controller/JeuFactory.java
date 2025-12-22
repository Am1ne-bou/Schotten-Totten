package com.schottenTotten.controller;

/**
 * Factory pour créer des instances de Jeu selon la variante choisie.
 * Utilise le design pattern Factory pour faciliter l'ajout de nouvelles variantes.
 */
public class JeuFactory {
    
    /**
     * Crée un jeu selon la variante spécifiée.
     */
    public static Jeu creerJeu(VarianteJeu variante, String nomJoueur1, boolean isAI1, 
                                String nomJoueur2, boolean isAI2) {
        if (variante == null) {
            throw new IllegalArgumentException("La variante ne peut pas être null");
        }
        
        return new Jeu(nomJoueur1, isAI1, nomJoueur2, isAI2);
    }
    
    /**
     * Crée un jeu avec la variante de base.
     */
    public static Jeu creerJeuBase(String nomJoueur1, boolean isAI1, 
                                    String nomJoueur2, boolean isAI2) {
        return creerJeu(VarianteJeu.BASE, nomJoueur1, isAI1, nomJoueur2, isAI2);
    }
    
    /**
     * Crée un jeu avec la variante tactique.
     */
    public static Jeu creerJeuTactique(String nomJoueur1, boolean isAI1, 
                                        String nomJoueur2, boolean isAI2) {
        return creerJeu(VarianteJeu.TACTIQUE, nomJoueur1, isAI1, nomJoueur2, isAI2);
    }
}
