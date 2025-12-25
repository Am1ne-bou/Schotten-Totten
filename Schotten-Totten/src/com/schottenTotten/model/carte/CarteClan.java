package com.schottenTotten.model.carte;

import com.schottenTotten.model.enums.Couleur;

public class CarteClan extends Carte {

    public CarteClan(Couleur couleur, int valeur) {
        super(couleur, valeur);
    }

    @Override
    public boolean isTactique() {
        return false;
    }
}