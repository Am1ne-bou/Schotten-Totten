package com.schottenTotten.model;

import java.util.ArrayList;
import java.util.List;

public class DeckTactique extends Deck {
    private List<CarteTactique> defausse;

    public DeckTactique() {
        super(false);
        this.defausse=new ArrayList<>();
        initialiserDeck();
    }

    @Override
    protected void initialiserDeck() {
        // Troupes d'élite
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

    public void defausser(CarteTactique carte) { defausse.add(carte); }
    public List<CarteTactique> getDefausse() { return new ArrayList<>(defausse); }
    public int getDefausseSize() { return defausse.size(); }
}