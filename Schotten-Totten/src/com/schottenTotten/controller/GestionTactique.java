package com.schottenTotten.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.schottenTotten.model.*;
import com.schottenTotten.model.carte.Carte;
import com.schottenTotten.model.carte.CarteClan;
import com.schottenTotten.model.carte.CarteTactique;
import com.schottenTotten.model.decks.Deck;
import com.schottenTotten.model.decks.DeckTactique;
import com.schottenTotten.model.enums.Couleur;
import com.schottenTotten.model.enums.TypeCarteTactique;
import com.schottenTotten.view.ConsoleView;

public class GestionTactique {
    private Jeu jeu;
    private ConsoleView view;

    public GestionTactique(Jeu jeu, ConsoleView view) {
        this.jeu=jeu;
        this.view=view;
    }

    public boolean peutJouerCarteTactique(Joueur joueur, Joueur adversaire) {
        return joueur.getCartesTactiquesJouees()<=adversaire.getCartesTactiquesJouees() && joueur.hasCarteTactique();
    }

    public void configurerTroupeElite(CarteTactique carte, Couleur couleur, int valeur) {
        TypeCarteTactique type=carte.getType();
        switch (type) {
            case JOKER:
                if (valeur<1 || valeur>9) throw new IllegalArgumentException("Valeur invalide pour le Joker (1-9)");
                break;
            case ESPION:
                valeur=7;
                break;
            case PORTE_BOUCLIER:
                if (valeur<1 || valeur>3) throw new IllegalArgumentException("Valeur invalide pour le Porte-Bouclier (1-3)");
                break;
            default:
                throw new IllegalArgumentException("Ce n'est pas une troupe d'élite");
        }
        carte.setCouleur(couleur);
        carte.setValeur(valeur);
    }

    public boolean executerCarteTactique(CarteTactique carte, Joueur joueur, int joueurNum) {
        TypeCarteTactique type=carte.getType();
        if (type.getCategorie()== TypeCarteTactique.TypeCategorie.TROUPE_ELITE) return executerTroupeElite(carte,joueur,joueurNum);
        if (type.getCategorie()== TypeCarteTactique.TypeCategorie.MODE_COMBAT) return executerModeCombat(carte,joueur);
        if (type.getCategorie()== TypeCarteTactique.TypeCategorie.RUSE) return executerRuse(carte,joueur,joueurNum);
        return false;
    }

    // Demande une borne et vérifie sa validité selon le critère
    private Borne demanderBorneValide(Joueur joueur, boolean checkLocked) {
        int index=view.demanderBorne(joueur,jeu.getVariante().getNombreBornes());
        Borne borne=jeu.getPlateau().getBorne(index);
        while (checkLocked ? borne.isLocked() : !jeu.peutAjouterCarte(borne,jeu.getJoueurCourant())) {
            view.afficherMessage(checkLocked ? "Cette borne est déjà revendiquée." : "Borne invalide.");
            index=view.demanderBorne(joueur,jeu.getVariante().getNombreBornes());
            borne=jeu.getPlateau().getBorne(index);
        }
        return borne;
    }

    private boolean executerTroupeElite(CarteTactique carte, Joueur joueur, int joueurNum) {
        Couleur couleur=view.demanderCouleur();
        int valeur;

        switch (carte.getType()) {
            case JOKER:
                valeur=view.demanderValeur(1,9);
                break;
            case ESPION:
                valeur=7;
                view.afficherMessage("L'Espion a une valeur fixe de 7.");
                break;
            case PORTE_BOUCLIER:
                valeur=view.demanderValeur(1,3);
                break;
            default:
                return false;
        }

        configurerTroupeElite(carte,couleur,valeur);
        Borne borne=demanderBorneValide(joueur,false);
        int indexBorne=getBorneIndex(borne);

        jeu.ajouterCarte(borne,joueurNum,carte);
        view.afficherMessage("Troupe d'élite jouée sur la borne "+indexBorne);
        return true;
    }

    // Récupère l'index d'une borne sur le plateau
    private int getBorneIndex(Borne borne) {
        for (int i=0; i<jeu.getVariante().getNombreBornes(); i++) {
            if (jeu.getPlateau().getBorne(i)==borne) return i;
        }
        return -1;
    }

    private boolean executerModeCombat(CarteTactique carte, Joueur joueur) {
        view.afficherMessage("Choisissez une borne pour "+carte.getType().getNom());
        Borne borne=demanderBorneValide(joueur,true);
        int indexBorne=getBorneIndex(borne);

        switch (carte.getType()) {
            case COLIN_MAILLARD:
                borne.setColinMaillard(true);
                break;
            case COMBAT_DE_BOUE:
                borne.setCombatDeBoue(true);
                break;
            default:
                return false;
        }

        view.afficherModeCombatApplique(carte.getType().getNom(),indexBorne);
        return true;
    }

    private boolean executerRuse(CarteTactique carte, Joueur joueur, int joueurNum) {
        int adversaireNum=(joueurNum==1) ? 2 : 1;
        switch (carte.getType()) {
            case CHASSEUR_DE_TETE: return executerChasseurDeTete(joueur);
            case STRATEGIE: return executerStratege(joueur,joueurNum);
            case BANSHEE: return executerBanshee(adversaireNum);
            case TRAITRE: return executerTraitre(joueurNum,adversaireNum);
            default: return false;
        }
    }

    private boolean executerChasseurDeTete(Joueur joueur) {
        view.afficherMessage("=== Chasseur de Tête ===");
        view.afficherMessage("Piochez 3 cartes, gardez-en 2.");

        List<Carte> piochees=new ArrayList<>();
        Deck deck=jeu.getDeckClan();
        DeckTactique deckTactique=jeu.getDeckTactique();

        for (int i=0; i<3; i++) {
            int choix=view.demanderChoixPioche(!deck.isEmpty(),deckTactique!=null && !deckTactique.isEmpty());
            if (choix==1 && !deck.isEmpty()) {
                piochees.addAll(deck.piocher(1));
            } else if (choix==2 && deckTactique!=null && !deckTactique.isEmpty()) {
                piochees.addAll(deckTactique.piocher(1));
            }
        }

        if (piochees.isEmpty()) {
            view.afficherMessage("Aucune carte piochée!");
            return false;
        }

        view.afficherMessage("Cartes piochées:");
        for (int i=0; i<piochees.size(); i++) {
            view.afficherMessage(i+": "+piochees.get(i));
        }

        int indexDefausse=view.demanderIndex("Carte à défausser",piochees.size());
        Carte defaussee=piochees.remove(indexDefausse);

        for (Carte c : piochees) joueur.addCartesToHand(Collections.singletonList(c));
        view.afficherMessage("Cartes ajoutées à votre main.");
        return true;
    }

    private boolean executerStratege(Joueur joueur, int joueurNum) {
        view.afficherMessage("=== Stratège ===");
        Plateau plateau=jeu.getPlateau();
        int nombreBornes=jeu.getVariante().getNombreBornes();

        int borneSource=view.demanderIndex("Borne source",nombreBornes);
        Borne source=plateau.getBorne(borneSource);
        List<Carte> carteClans =source.getCartesParJoueur(joueurNum);

        if (carteClans.isEmpty()) {
            view.afficherMessage("Aucune carte sur cette borne!");
            return false;
        }

        view.afficherMessage("Cartes:");
        for (int i = 0; i< carteClans.size(); i++) {
            view.afficherMessage(i+": "+ carteClans.get(i));
        }

        int indexCarte=view.demanderIndex("Carte à déplacer/défausser", carteClans.size());
        int action=view.demanderChoixStratege();

        if (action==1) {
            int borneDest=view.demanderIndex("Borne destination",nombreBornes);
            Borne dest=plateau.getBorne(borneDest);

            if (!jeu.peutAjouterCarte(dest,joueurNum)) {
                view.afficherMessage("Destination invalide!");
                return false;
            }

            Carte carteClan =jeu.retirerCarte(source,joueurNum,indexCarte);
            jeu.ajouterCarte(dest,joueurNum, carteClan);
            view.afficherMessage("Carte déplacée.");
        } else {
            jeu.retirerCarte(source,joueurNum,indexCarte);
            view.afficherMessage("Carte défaussée.");
        }
        return true;
    }

    // Récupère les indices des cartes clan (non tactiques) d'un joueur sur une borne
    private List<Integer> getIndicesCartesClan(List<Carte> carteClans) {
        List<Integer> indices=new ArrayList<>();
        for (int i = 0; i< carteClans.size(); i++) {
            if (!(carteClans.get(i) instanceof CarteTactique)) indices.add(i);
        }
        return indices;
    }

    private boolean executerBanshee(int adversaireNum) {
        view.afficherMessage("=== Banshee ===");
        Plateau plateau=jeu.getPlateau();
        int nombreBornes=jeu.getVariante().getNombreBornes();

        int indexBorne=view.demanderIndex("Borne cible",nombreBornes);
        Borne borne=plateau.getBorne(indexBorne);

        if (borne.isLocked()) {
            view.afficherMessage("Borne déjà revendiquée!");
            return false;
        }

        List<Carte> cartesAdversaire=borne.getCartesParJoueur(adversaireNum);
        if (cartesAdversaire.isEmpty()) {
            view.afficherMessage("Aucune carte adverse!");
            return false;
        }

        List<Integer> indicesValides=getIndicesCartesClan(cartesAdversaire);
        if (indicesValides.isEmpty()) {
            view.afficherMessage("Aucune carte clan à défausser!");
            return false;
        }

        view.afficherMessage("Cartes adverses (clan uniquement):");
        for (int i=0; i<indicesValides.size(); i++) {
            view.afficherMessage(i+": "+cartesAdversaire.get(indicesValides.get(i)));
        }

        int choix=view.demanderIndex("Carte à défausser",indicesValides.size());
        jeu.retirerCarte(borne,adversaireNum,indicesValides.get(choix));
        view.afficherMessage("Carte défaussée!");
        return true;
    }

    private boolean executerTraitre(int joueurNum, int adversaireNum) {
        view.afficherMessage("=== Traître ===");
        Plateau plateau=jeu.getPlateau();
        int nombreBornes=jeu.getVariante().getNombreBornes();

        int borneSource=view.demanderIndex("Borne source (adverse)",nombreBornes);
        Borne source=plateau.getBorne(borneSource);

        if (source.isLocked()) {
            view.afficherMessage("Borne déjà revendiquée!");
            return false;
        }

        List<Carte> cartesAdversaire=source.getCartesParJoueur(adversaireNum);
        if (cartesAdversaire.isEmpty()) {
            view.afficherMessage("Aucune carte adverse!");
            return false;
        }

        List<Integer> indicesValides=getIndicesCartesClan(cartesAdversaire);
        if (indicesValides.isEmpty()) {
            view.afficherMessage("Aucune carte clan à voler!");
            return false;
        }

        view.afficherMessage("Cartes adverses (clan uniquement):");
        for (int i=0; i<indicesValides.size(); i++) {
            view.afficherMessage(i+": "+cartesAdversaire.get(indicesValides.get(i)));
        }

        int choix=view.demanderIndex("Carte à voler",indicesValides.size());
        Carte carteClan =jeu.retirerCarte(source,adversaireNum,indicesValides.get(choix));

        int borneDest=view.demanderIndex("Borne destination",nombreBornes);
        Borne dest=plateau.getBorne(borneDest);

        if (!jeu.peutAjouterCarte(dest,joueurNum)) {
            view.afficherMessage("Destination invalide!");
            return false;
        }

        jeu.ajouterCarte(dest,joueurNum, carteClan);
        view.afficherMessage("Carte volée et placée!");
        return true;
    }
}