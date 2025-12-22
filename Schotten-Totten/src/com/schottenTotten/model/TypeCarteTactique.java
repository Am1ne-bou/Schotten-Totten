package com.schottenTotten.model;


public enum TypeCarteTactique {
    // Cartes Personnages
    JOKER("Joker", "Peut remplacer n'importe quelle carte clan", TypeCategorie.PERSONNAGE),
    ESPION("Espion", "Prend la valeur 7 et peut être de n'importe quelle couleur", TypeCategorie.PERSONNAGE),
    PORTE_BOUCLIER("Porte-Bouclier", "Prend la valeur 1, 2 ou 3 et peut être de n'importe quelle couleur", TypeCategorie.PERSONNAGE),
    
    // Cartes Actions (modes Ruse)
    COLIN_MAILLARD("Colin-Maillard", "Regardez la main de votre adversaire", TypeCategorie.RUSE),
    COMBAT_DE_BOUE("Combat de Boue", "Retournez 2 cartes clan déjà jouées", TypeCategorie.RUSE),
    CHASSEUR_DE_TETE("Chasseur de Tête", "Récupérez une carte clan défaussée", TypeCategorie.RUSE),
    STRATEGIE("Stratégie", "Piochez 3 cartes, gardez-en 1 et défaussez le reste", TypeCategorie.RUSE),
    BANSHEE("Banshee", "Retournez une carte tactique adverse", TypeCategorie.RUSE),
    TRAITRE("Traître", "Retournez votre propre carte clan", TypeCategorie.RUSE);

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

    @Override
    public String toString() {
        return nom;
    }

    public enum TypeCategorie {
        PERSONNAGE("Personnage - joué sur le plateau"),
        RUSE("Ruse - action immédiate");

        private final String description;

        TypeCategorie(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}