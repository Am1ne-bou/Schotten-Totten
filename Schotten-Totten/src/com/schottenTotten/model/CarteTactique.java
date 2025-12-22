package com.schottenTotten.model;

public class CarteTactique extends Carte {
    private TypeCarteTactique type;

    public CarteTactique(TypeCarteTactique type) {
        super(null, 0); // Les cartes tactiques n'ont pas de couleur ni de valeur
        this.type = type;
    }

    public TypeCarteTactique getType() {
        return type;
    }

    public CarteTactique creerCarteTactique(TypeCarteTactique type) {
        return new CarteTactique(type);
    }

    @Override
    public String toString() {
        return type.getNom();
    }
    
}
