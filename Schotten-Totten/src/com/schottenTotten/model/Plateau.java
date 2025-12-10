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

    private int getNombreBornesControlees(int joueur) {
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

    private boolean hewon(int joueur) {
        if (getNombreBornesControlees(joueur) >= 5) {
            return true;
        }

        // Vérification des 3 bornes consécutives
        int suite = 0;
        for (int i = 0; i < 9; i++) {
            if (bornes[i].proprietaire == joueur) {
                suite++;
                if (suite >= 3) {
                    return true;
                }
            } else {
                suite = 0;
            }
        }
        return false;
    }

    public boolean gameended() {
        return hewon(1) || hewon(2);
    }

    public int getwinner() {
        if (hewon(1)) return 1;
        if (hewon(2)) return 2;
        return 0;
    }


}