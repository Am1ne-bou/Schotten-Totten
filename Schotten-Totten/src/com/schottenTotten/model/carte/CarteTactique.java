package com.schottenTotten.model.carte;

import com.schottenTotten.model.enums.Couleur;
import com.schottenTotten.model.enums.TypeCarteTactique;

public class CarteTactique extends Carte {
    private final TypeCarteTactique type;

    public CarteTactique(TypeCarteTactique type) {
        super();
        this.type = type;
    }

    public TypeCarteTactique getType() {
        return type;
    }


    public boolean isTroupeElite() {
        return TypeCarteTactique.TypeCategorie.TROUPE_ELITE == type.getCategorie() ;
    }

    public boolean isModeCombat() {
        return TypeCarteTactique.TypeCategorie.MODE_COMBAT == type.getCategorie() ;
    }

    public boolean isRuse() {
        return TypeCarteTactique.TypeCategorie.RUSE == type.getCategorie() ;
    }

    public void setCouleur(Couleur couleur){
        this.couleur=couleur;
    }
    public void setValeur(int valeur){
        this.valeur=valeur;
    }

    @Override
    public int getValeur() {
        return isTroupeElite() ? valeur : 0;
    }

    @Override
    public boolean isTactique() {
        return true;
    }
}