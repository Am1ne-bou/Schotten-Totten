package com.schottenTotten.model.decks;

import com.schottenTotten.model.carte.CarteTactique;
import com.schottenTotten.model.enums.TypeCarteTactique;

public class DeckTactique extends Deck {

    public DeckTactique() {
        super();
    }

    @Override
    public void initialiserDeck() {
        // Troupes d'élite
        cartes.add(new CarteTactique(TypeCarteTactique.JOKER));
        cartes.add(new CarteTactique(TypeCarteTactique.JOKER));
        cartes.add(new CarteTactique(TypeCarteTactique.ESPION));
        cartes.add(new CarteTactique(TypeCarteTactique.PORTE_BOUCLIER));
        // Modes de combat
        cartes.add(new CarteTactique(TypeCarteTactique.COLIN_MAILLARD));
        cartes.add(new CarteTactique(TypeCarteTactique.COMBAT_DE_BOUE));
        // Ruses
        cartes.add(new CarteTactique(TypeCarteTactique.CHASSEUR_DE_TETE));
        cartes.add(new CarteTactique(TypeCarteTactique.STRATEGIE));
        cartes.add(new CarteTactique(TypeCarteTactique.BANSHEE));
        cartes.add(new CarteTactique(TypeCarteTactique.TRAITRE));
    }

}