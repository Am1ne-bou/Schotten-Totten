package com.schottenTotten.model;

public class Plateau {
    private final Borne[] bornes;

    public Plateau(int nbrBornes) {
        this.bornes=new Borne[nbrBornes];
        for (int i=0; i<nbrBornes; i++) {
            bornes[i]=new Borne();
        }
    }

    public Plateau() { this(9); }

    public Borne getBorne(int index) {
        if (index<0 || index>=bornes.length) {
            throw new IllegalArgumentException("Index de borne invalide: "+index);
        }
        return bornes[index];
    }

    public int getIndexCombatDeBoue(int nbrBornes){
        for (int b=0; b<nbrBornes; b++) {
            if (getBorne(b).hasCombatDeBoue()) return b;
        }
        return -1;
    }

    public int getNombreBornes() { return bornes.length; }
    public Borne[] getAllBornes() { return bornes; }
}