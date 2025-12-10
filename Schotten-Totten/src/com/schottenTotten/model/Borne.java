package com.schottenTotten.model;

import java.util.*;

public class Borne {

    public List<Carte> cartesJ1;
    public List<Carte> cartesJ2;
    public boolean locked;
    public int lastPlayer;   // 0 = aucun, 1 = joueur 1, 2 = joueur 2
    public int proprietaire; // 0 = aucun, 1 = joueur 1, 2 = joueur 2

    public Borne() {
        this.cartesJ1 = new ArrayList<>(3);
        this.cartesJ2 = new ArrayList<>(3);
        this.locked = false;
        this.lastPlayer = 0;
        this.proprietaire = 0;
    }

    public List<Carte> getCartesJ1() {
        return cartesJ1;
    }

    public List<Carte> getCartesJ2() {
        return cartesJ2;
    }
    public boolean isLocked() {
        return locked;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getProprietaire() {
        return proprietaire;
    }
    public void setProprietaire(int proprietaire) {
        this.proprietaire = proprietaire;
    }

    public void addCarte(int joueur, Carte carte) {
        if (joueur == 1) {
            if (cartesJ1.size() < 3) {
                cartesJ1.add(carte);
            } else {
                throw new IllegalStateException("Le joueur 1 ne peut pas ajouter plus de cartes à cette borne.");
            }
        } else if (joueur == 2) {
            if (cartesJ2.size() < 3) {
                cartesJ2.add(carte);
            } else {
                throw new IllegalStateException("Le joueur 2 ne peut pas ajouter plus de cartes à cette borne.");
            }
        } else {
            throw new IllegalArgumentException("Numéro de joueur invalide.");
        }
        lastPlayer = joueur;
    }

    public boolean isFull() {
        return cartesJ1.size() == 3 && cartesJ2.size() == 3;
    }

    public int compareCartes() {
        if (!isFull()) {
            throw new IllegalStateException("Les deux joueurs doivent avoir joué 3 cartes chacun pour comparer.");
        }

        int typeJ1 = TypeofHand(cartesJ1);
        int typeJ2 = TypeofHand(cartesJ2);

        if (typeJ1 > typeJ2) {
            this.proprietaire = 1;
            this.locked = true;
            return 1; // Joueur 1 gagne
        } else if (typeJ2 > typeJ1) {
            this.proprietaire = 2;
            this.locked = true;
            return 2; // Joueur 2 gagne
        } else {
            int sommeJ1 = sommeMain(cartesJ1);
            int sommeJ2 = sommeMain(cartesJ2);
            if (sommeJ1 > sommeJ2) {
                this.proprietaire = 1;
                this.locked = true;
                return 1; // Joueur 1 gagne
            } else if (sommeJ2 > sommeJ1) {
                this.proprietaire = 2;
                this.locked = true;
                return 2; // Joueur 2 gagne
            } else {
                this.proprietaire = lastPlayer;
                this.locked = true;
                return lastPlayer; // Égalité
            }
        }
    }

    public boolean isOwned() {
        return proprietaire != 0;
    }


    public int TypeofHand(List<Carte> cartes) { // 0: somme, 1: suite, 2: couleur, 3: brelan, 4: suite couleur
        List<Couleur> couleurs = new ArrayList<>();
        List<Integer> valeurs = new ArrayList<>();

        for (Carte carte : cartes) {
            int valeur = carte.getValeur();
            valeurs.add(valeur);
            couleurs.add(carte.getCouleur());
        }

        Collections.sort(valeurs);

        boolean isSuite = (valeurs.get(2) - valeurs.get(0) == 2) && (valeurs.get(1) - valeurs.get(0) == 1);
        boolean isCouleur = (couleurs.get(0).equals(couleurs.get(1))) && (couleurs.get(1).equals(couleurs.get(2)));
        boolean isBrelan = (valeurs.get(0).equals(valeurs.get(1)) && valeurs.get(1).equals(valeurs.get(2)));

        if (isSuite && isCouleur) {
            return 4; // suite couleur
        } else if (isBrelan) {
            return 3; // brelan
        } else if (isCouleur) {
            return 2; // couleur
        } else if (isSuite) {
            return 1; // suite
        } else {
            return 0; // somme
        }    
    }

    private int sommeMain(List<Carte> cartes) {
        int somme = 0;
        for (Carte carte : cartes) {
            somme += carte.getValeur();
        }
        return somme;
    }
}