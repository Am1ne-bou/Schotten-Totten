package com.schottenTotten.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.schottenTotten.ai.IAAleatoire;
import com.schottenTotten.model.*;
import com.schottenTotten.view.ConsoleView;

public class Jeu{
    private Plateau plateau;
    private Deck deck;
    private Joueur joueur1;
    private Joueur joueur2;
    private int joueurCourant; // 1 ou 2
    private boolean gameEnded;
    private ConsoleView consoleView;

    private void changerJoueurCourant() {
        joueurCourant = (joueurCourant == 1) ? 2 : 1;
    }

    public Jeu(String nomJoueur1, boolean isAI1, String nomJoueur2, boolean isAI2) {
        this.plateau = new Plateau();
        this.deck = new Deck();
        this.deck.shuffle();
        this.joueur1 = new Joueur(nomJoueur1, isAI1 );
        this.joueur2 = new Joueur(nomJoueur2, isAI2);
        this.joueurCourant = 1;
        this.gameEnded = false;
        this.consoleView = new ConsoleView();
    }

    public Plateau getPlateau() {
        return plateau;
    }
    public Deck getDeck() {
        return deck;
    }
    public Joueur getJoueur1() {
        return joueur1;
    }

    public Joueur getJoueur2() {
        return joueur2;
    }
    public int getJoueurCourant() {
        return joueurCourant;
    }

    public  Carte choseCarteToPlay(Joueur joueur) {
        if (joueur.isAI()) {
            return IAAleatoire.choisirCarte(joueur);
        } else {
            return consoleView.AskCarteFromPlayer(joueur, joueur.getHand().size());
        }
    }

    
    private void jouerCarte(Joueur joueur){
        Carte carteAJouer= choseCarteToPlay(joueur);
        while(!joueur.getHand().contains(carteAJouer)){
            carteAJouer= choseCarteToPlay(joueur);
        }
        int borneIndex = consoleView.choseBorneToPlay(joueur, 9);
        Borne borne = plateau.getBornes(borneIndex);
        while( borne.isLocked()){
            borneIndex = consoleView.choseBorneToPlay(joueur, 9);
            borne = plateau.getBornes(borneIndex);
        }
        borne.addCarte(joueurCourant, carteAJouer);
        joueur.removeCarteFromHand(carteAJouer);  
    }

    private void jouerCarteIA(Joueur joueur){
        Carte carteAJouer= IAAleatoire.choisirCarte(joueur);
        int borneIndex = IAAleatoire.choisirBorne(plateau, 9);
        Borne borne = plateau.getBornes(borneIndex);
        while( borne.isLocked()|| (joueurCourant==1 ? borne.getCartesJ1().size()>=3 : borne.getCartesJ2().size()>=3) ){
            borneIndex = IAAleatoire.choisirBorne(plateau, 9);
            borne = plateau.getBornes(borneIndex);
        }
        borne.addCarte(joueurCourant, carteAJouer);
        joueur.removeCarteFromHand(carteAJouer);  
    }

    private boolean isGameEnded() {
        return gameEnded;
    }

    private void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    private void distribuer1(int nbrCartes){
        for (int i = 0; i < nbrCartes; i++) {
            joueur1.addCarteToHand(deck.piocher(1));
            joueur2.addCarteToHand(deck.piocher(1));
        }
    }

    private void piocherCarte(Joueur joueur, int nbrCartes){
        if(deck.getDeckSize()>=nbrCartes){
            joueur.addCarteToHand(deck.piocher(nbrCartes));
        }else{
            consoleView.afficherDeckVide();
            while (deck.getDeckSize()>0) {
                joueur.addCarteToHand(deck.piocher(1));
            }
        }
    }

    private Borne borneFulltoTest(){
        for (int i = 0; i < 9; i++) {
            Borne borne = plateau.getBornes(i);
            if (isFull(borne) && !borne.isLocked()) {
                return borne;
            }
        }
        return null;
    }

        public int compareCartes(Borne borne) {
        if (!isFull(borne)) {
            throw new IllegalStateException("Les deux joueurs doivent avoir joué 3 cartes chacun pour comparer.");
        }

        int typeJ1 = TypeofHand(borne.getCartesJ1());
        int typeJ2 = TypeofHand(borne.getCartesJ2());

        if (typeJ1 > typeJ2) {
            borne.setProprietaire(1);
            borne.setLocked(true);
            return 1; // Joueur 1 gagne
        } else if (typeJ2 > typeJ1) {
            borne.setProprietaire(2);
            borne.setLocked(true);
            return 2; // Joueur 2 gagne
        } else {
            int sommeJ1 = sommeMain(borne.getCartesJ1());
            int sommeJ2 = sommeMain(borne.getCartesJ2());
            if (sommeJ1 > sommeJ2) {
                borne.setProprietaire(1);
                borne.setLocked(true);
                return 1; // Joueur 1 gagne
            } else if (sommeJ2 > sommeJ1) {
                borne.setProprietaire(2);
                borne.setLocked(true);
                return 2; // Joueur 2 gagne
            } else {
                borne.setProprietaire(borne.getLastPlayer());
                borne.setLocked(true);
                return borne.getLastPlayer(); // Égalité
            }
        }
    }
    public boolean isFull(Borne borne) {
        return borne.getCartesJ1().size() == 3 && borne.getCartesJ2().size() == 3;
    }

    public boolean isOwned(Borne borne) {
        return borne.getProprietaire() != 0;
    }


    public int TypeofHand(List<Carte> cartes) { // 0: somme, 1: suite, 2: couleur, 3: brelan, 4: suite couleur
        List<Couleur> couleurs = new ArrayList<>();
        List<Integer> valeurs = new ArrayList<>();

        for (Carte carte : cartes) {
            int valeur = carte.getValeur();
            valeurs.add(valeur);
            couleurs.add(carte.getCouleur());
        }

        Collections.sort(valeurs);

        boolean isSuite = (valeurs.get(2) - valeurs.get(0) == 2) && (valeurs.get(1) - valeurs.get(0) == 1);
        boolean isCouleur = (couleurs.get(0).equals(couleurs.get(1))) && (couleurs.get(1).equals(couleurs.get(2)));
        boolean isBrelan = (valeurs.get(0).equals(valeurs.get(1)) && valeurs.get(1).equals(valeurs.get(2)));

        if (isSuite && isCouleur) {
            return 4; // suite couleur
        } else if (isBrelan) {
            return 3; // brelan
        } else if (isCouleur) {
            return 2; // couleur
        } else if (isSuite) {
            return 1; // suite
        } else {
            return 0; // somme
        }    
    }

    private int sommeMain(List<Carte> cartes) {
        int somme = 0;
        for (Carte carte : cartes) {
            somme += carte.getValeur();
        }
        return somme;
    }


    
    private boolean hewon(int joueur,Plateau plateau) {
        if (plateau.getNombreBornesControlees(joueur) >= 5) {
            return true;
        }

        // Vérification des 3 bornes consécutives
        int suite = 0;
        for (int i = 0; i < 9; i++) {
            if (plateau.getBornes(i).getProprietaire() == joueur) {
                suite++;
                if (suite >= 3) {
                    return true;
                }
            } else {
                suite = 0;
            }
        }
        return false;
    }

    public boolean gameended() {
        return hewon(1, plateau) || hewon(2, plateau);
    }

    public int getwinner() {
        if (hewon(1, plateau)) return 1;
        if (hewon(2, plateau)) return 2;
        return 0;
    }


    public void Gameloop(){

        distribuer1(6);

        consoleView.afficherPlateau(plateau, 9);

        while(!isGameEnded()){ 
            consoleView.afficherMainJoueur(joueurCourant == 1 ? joueur1 : joueur2);

            if(joueurCourant == 1){
                if(joueur1.isAI()){
                    jouerCarteIA(joueur1);
                } else {
                    jouerCarte(joueur1);
                }
            } else {
                if(joueur2.isAI()){
                    jouerCarteIA(joueur2);
                } else {
                    jouerCarte(joueur2);
                }
            }   

            Borne borne = borneFulltoTest();

            if(borne != null){
                consoleView.afficherBornePleine();
                int gagnant = compareCartes(borne);
                consoleView.afficherGagnantBorne(gagnant==1 ? joueur1 : joueur2);
            }

            setGameEnded(gameended());

            piocherCarte(joueurCourant == 1 ? joueur1 : joueur2, 1);

            changerJoueurCourant();

            consoleView.afficherPlateau(plateau, 9);

            consoleView.afficherSeparateur();
        }
        consoleView.afficherFinPartie(getwinner()==1 ? joueur1.getName() : joueur2.getName());
    }

}