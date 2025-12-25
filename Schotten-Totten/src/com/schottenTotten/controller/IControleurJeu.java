package com.schottenTotten.controller;

import com.schottenTotten.model.*;
import com.schottenTotten.model.carte.Carte;
import com.schottenTotten.model.carte.CarteTactique;
import com.schottenTotten.model.decks.DeckClan;
import com.schottenTotten.model.decks.DeckTactique;
import com.schottenTotten.model.enums.Couleur;

/**
 * Interface globale définissant toutes les opérations du jeu
 * incluant la gestion tactique
 */
public interface IControleurJeu {

    // === Opérations de jeu de base ===
    Plateau getPlateau();
    VarianteJeu getVariante();
    int getJoueurCourant();
    Joueur getJoueur(int numero);

    DeckClan getDeckClan();
    DeckTactique getDeckTactique();

    boolean peutAjouterCarte(Borne borne, int joueur);
    void ajouterCarte(Borne borne, int joueur, Carte carte);
    Carte retirerCarte(Borne borne, int joueur, int index);

    // === Gestion tactique ===
    boolean peutJouerCarteTactique(Joueur joueur, Joueur adversaire);
    boolean executerCarteTactique(CarteTactique carte, Joueur joueur, int joueurNum);
    void configurerTroupeElite(CarteTactique carte, Couleur couleur, int valeur);

    // === Contrôle du jeu ===
    void lancerPartie();
    boolean estTermine();
    int getGagnant();
}