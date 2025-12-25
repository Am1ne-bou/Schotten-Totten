package com.schottenTotten.model.decks;

import com.schottenTotten.model.carte.Carte;
import com.schottenTotten.model.carte.CarteClan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Deck {
    List<Carte> cartes;

    public Deck() {
        this.cartes = new ArrayList<>();
        initialiserDeck();
    }


    public void initialiserDeck() {

    }

    public void shuffle() {
        Collections.shuffle(cartes);
    }

    public List<Carte> piocher(int nbrCartes) {
        List<Carte> cartesPiochees = new ArrayList<>();
        int nbAPiocher = Math.min(nbrCartes, cartes.size());
        for (int i = 0; i < nbAPiocher; i++) {
            cartesPiochees.add(cartes.remove(i));
        }
        return cartesPiochees;
    }

    public int getDeckSize() {
        return cartes.size();
    }

    public boolean isEmpty() {
        return cartes.isEmpty();
    }
}