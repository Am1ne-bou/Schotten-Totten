package com.schottenTotten.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    protected List<Carte> cartes;

    public Deck() {
        this.cartes=new ArrayList<>();
        initialiserDeck();
    }

    protected Deck(boolean initialiser) {
        this.cartes=new ArrayList<>();
        if (initialiser) initialiserDeck();
    }

    protected void initialiserDeck() {
        for (Couleur couleur : Couleur.values()) {
            for (int valeur=1; valeur<=9; valeur++) {
                cartes.add(new Carte(couleur,valeur));
            }
        }
    }

    public void shuffle() { Collections.shuffle(cartes); }

    public List<Carte> piocher(int nbrCartes) {
        if (cartes.isEmpty()) return new ArrayList<>();
        int nbAPiocher=Math.min(nbrCartes,cartes.size());
        List<Carte> cartesPiochees=new ArrayList<>(cartes.subList(0,nbAPiocher));
        cartes.subList(0,nbAPiocher).clear();
        return cartesPiochees;
    }

    public int getDeckSize() { return cartes.size(); }
    public boolean isEmpty() { return cartes.isEmpty(); }
}