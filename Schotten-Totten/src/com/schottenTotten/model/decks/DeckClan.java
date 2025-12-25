package com.schottenTotten.model.decks;

import com.schottenTotten.model.carte.CarteClan;
import com.schottenTotten.model.enums.Couleur;

public class DeckClan extends Deck {
    public DeckClan() {
        super();
    }

    @Override
    public void initialiserDeck() {
        for (Couleur couleur : Couleur.values()) {
            for (int valeur = 1; valeur <= 9; valeur++) {
                cartes.add(new CarteClan(couleur, valeur));
            }
        }
    }

}