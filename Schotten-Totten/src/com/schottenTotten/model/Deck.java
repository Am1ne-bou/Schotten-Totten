package com.schottenTotten.model;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private List<Carte> cartes;
    //initialisation du deck avec toutes les cartes
    public Deck(){
        this.cartes = new ArrayList<>();
        for (Couleur couleur : Couleur.values()) {
            for (int valeur = 1; valeur <= 9; valeur++) {
                cartes.add(new Carte(couleur, valeur));
            }
        }
    }
    //schuffle les cartes du deck
    public void shuffle(){
        java.util.Collections.shuffle(cartes);
    }
    //pioche une carte du deck
    public List<Carte> piocher(int nbrCartes){
        if (cartes.isEmpty()) {
            throw new IllegalStateException("Le deck est vide.");
        }
        if(nbrCartes > cartes.size()){
            throw new IllegalArgumentException("Le nombre de cartes à piocher dépasse le nombre de cartes restantes dans le deck.");
        }
        List<Carte> cartesPiochees = new ArrayList<>(cartes.subList(0, nbrCartes));
        cartes.subList(0, nbrCartes).clear();
        return cartesPiochees;
    }

    public int getDeckSize(){
        return cartes.size();
    }
    
    
}