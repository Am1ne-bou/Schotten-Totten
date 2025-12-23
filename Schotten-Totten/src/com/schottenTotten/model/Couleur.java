package com.schottenTotten.model;

public enum Couleur {
    ROUGE("R"),
    BLEU("B"),
    VERT("V"),
    JAUNE("J"),
    ORANGE("O"),
    VIOLET("P");

    private final String abreviation;

    Couleur(String abreviation) { this.abreviation=abreviation; }
    public String getAbreviation() { return abreviation; }
}