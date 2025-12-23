package com.schottenTotten.model;

public class CarteTactique extends Carte {
    private TypeCarteTactique type;
    private Integer valeurChoisie;
    private Couleur couleurChoisie;

    public CarteTactique(TypeCarteTactique type) {
        super(null,0);
        this.type=type;
        this.valeurChoisie=null;
        this.couleurChoisie=null;
    }

    public TypeCarteTactique getType() { return type; }
    public Integer getValeurChoisie() { return valeurChoisie; }
    public Couleur getCouleurChoisie() { return couleurChoisie; }

    @Override
    public int getValeur() {
        return (type.isTroupeElite() && valeurChoisie!=null) ? valeurChoisie : 0;
    }


    public void setValeurChoisie(Integer valeur) { this.valeurChoisie=valeur; }
    public void setCouleurChoisie(Couleur couleur) { this.couleurChoisie=couleur; }

    public boolean isConfiguree() {
        return !type.isTroupeElite() || (valeurChoisie!=null && couleurChoisie!=null);
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append("[").append(type.getNom()).append("]");
        if (type.isTroupeElite() && isConfiguree()) {
            sb.append(" -> ").append(valeurChoisie).append("-").append(couleurChoisie.getAbreviation());
        }
        return sb.toString();
    }
}