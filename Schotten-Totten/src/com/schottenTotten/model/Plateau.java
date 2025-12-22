package com.schottenTotten.model;

public class Plateau {
    private Borne[] bornes;

    public Plateau() {
        this.bornes = new Borne[9];
        for (int i = 0; i < 9; i++) {
            bornes[i] = new Borne();
        }   
    }

    public Borne getBornes(int index) {
        if(index < 0 || index >= bornes.length) {
            throw new IllegalArgumentException("Index de borne invalide.");
        }
        return bornes[index];
    }

    public int getNombreBornesControlees(int joueur) {
        if (joueur != 1 && joueur != 2) {
            throw new IllegalArgumentException("Le joueur doit être 1 ou 2.");
        }
        int count = 0;
        for (Borne borne : bornes) {
            if (borne.proprietaire == joueur) {
                count++;
            }
        }
        return count;
    }


}