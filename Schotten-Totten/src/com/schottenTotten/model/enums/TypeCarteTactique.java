package com.schottenTotten.model.enums;

public enum TypeCarteTactique {
    // Troupes d'élite
    JOKER("Joker", "Peut prendre n'importe quelle couleur et valeur (1-9)", TypeCategorie.TROUPE_ELITE),
    ESPION("Espion", "Valeur fixe de 7, couleur au choix", TypeCategorie.TROUPE_ELITE),
    PORTE_BOUCLIER("Porte-Bouclier", "Valeur 1, 2 ou 3, couleur au choix", TypeCategorie.TROUPE_ELITE),
    // Modes de combat
    COLIN_MAILLARD("Colin-Maillard", "Seule la somme compte pour cette borne", TypeCategorie.MODE_COMBAT),
    COMBAT_DE_BOUE("Combat de Boue", "4 cartes requises au lieu de 3", TypeCategorie.MODE_COMBAT),
    // Ruses
    CHASSEUR_DE_TETE("Chasseur de Tête", "Piochez 3 cartes, gardez-en 2", TypeCategorie.RUSE),
    STRATEGIE("Stratège", "Déplacez ou défaussez une de vos cartes", TypeCategorie.RUSE),
    BANSHEE("Banshee", "Défaussez une carte clan adverse", TypeCategorie.RUSE),
    TRAITRE("Traître", "Volez une carte clan adverse", TypeCategorie.RUSE);

    private final String nom;
    private final String description;
    private final TypeCategorie categorie;

    TypeCarteTactique(String nom, String description, TypeCategorie categorie) {
        this.nom = nom;
        this.description = description;
        this.categorie = categorie;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public TypeCategorie getCategorie() {
        return categorie;
    }


    public enum TypeCategorie {
        TROUPE_ELITE,
        MODE_COMBAT,
        RUSE
    }
}