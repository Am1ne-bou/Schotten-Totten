package com.schottenTotten.model;

import java.util.*;

public class Borne {
    private List<Carte> cartesJ1;
    private List<Carte> cartesJ2;
    private boolean locked;
    private int lastPlayer;
    private int proprietaire;
    private boolean colinMaillard;
    private boolean combatDeBoue;

    public Borne() {
        this.cartesJ1=new ArrayList<>();
        this.cartesJ2=new ArrayList<>();
        this.locked=false;
        this.lastPlayer=0;
        this.proprietaire=0;
        this.colinMaillard=false;
        this.combatDeBoue=false;
    }

    // Getter unifié pour les cartes d'un joueur
    public List<Carte> getCartes(int joueur) {
        if (joueur==1) return cartesJ1;
        if (joueur==2) return cartesJ2;
        throw new IllegalArgumentException("Joueur doit être 1 ou 2");
    }

    public List<Carte> getCartesJ1() { return cartesJ1; }
    public List<Carte> getCartesJ2() { return cartesJ2; }
    public int getNbCartes(int joueur) { return getCartes(joueur).size(); }
    public boolean isLocked() { return locked; }
    public int getProprietaire() { return proprietaire; }
    public int getLastPlayer() { return lastPlayer; }
    public boolean hasColinMaillard() { return colinMaillard; }
    public boolean hasCombatDeBoue() { return combatDeBoue; }

    public void setLocked(boolean locked) { this.locked=locked; }
    public void setProprietaire(int proprietaire) { this.proprietaire=proprietaire; }
    public void setLastPlayer(int lastPlayer) { this.lastPlayer=lastPlayer; }
    public void setColinMaillard(boolean colinMaillard) { this.colinMaillard=colinMaillard; }
    public void setCombatDeBoue(boolean combatDeBoue) { this.combatDeBoue=combatDeBoue; }

    // Méthodes unifiées pour ajouter/retirer des cartes
    public void addCarte(int joueur, Carte carte) {
        getCartes(joueur).add(carte);
        lastPlayer=joueur;
    }

    public Carte removeCarte(int joueur, int index) {
        return getCartes(joueur).remove(index);
    }
}