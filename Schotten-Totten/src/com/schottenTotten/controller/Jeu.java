package com.schottenTotten.controller;

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
    
    private void jouerCarte(Joueur joueur){
        Carte carteAJouer= consoleView.choseCarteToPlay(joueur);
        while(!joueur.getHand().contains(carteAJouer)){
            carteAJouer= consoleView.choseCarteToPlay(joueur);
        }
        int borneIndex = consoleView.choseBorneToPlay(joueur);
        Borne borne = plateau.getBornes(borneIndex);
        while( borne.isLocked()){
            borneIndex = consoleView.choseBorneToPlay(joueur);
            borne = plateau.getBornes(borneIndex);
        }
        borne.addCarte(joueurCourant, carteAJouer);
        joueur.removeCarteFromHand(carteAJouer);  
    }

    private void jouerCarteIA(Joueur joueur){
        Carte carteAJouer= IAAleatoire.choisirCarte(joueur);
        int borneIndex = IAAleatoire.choisirBorne(plateau);
        Borne borne = plateau.getBornes(borneIndex);
        while( borne.isLocked()|| (joueurCourant==1 ? borne.getCartesJ1().size()>=3 : borne.getCartesJ2().size()>=3) ){
            borneIndex = IAAleatoire.choisirBorne(plateau);
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
            System.out.println("Le deck est vide,/////.");
            while (deck.getDeckSize()>0) {
                joueur.addCarteToHand(deck.piocher(1));
            }
        }
    }

    private Borne borneFulltoTest(){
        for (int i = 0; i < 9; i++) {
            Borne borne = plateau.getBornes(i);
            if (borne.isFull() && !borne.isLocked()) {
                return borne;
            }
        }
        return null;
    }

    public void Gameloop(){

        distribuer1(6);

        consoleView.afficherPlateau(plateau);

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
                System.out.println("Une borne est pleine, évaluation des mains...");
                if(borne.compareCartes() == 1){
                    System.out.println("Le joueur 1 remporte la borne!");
                    borne.setProprietaire(1);
                    borne.setLocked(true);
                } else {
                    System.out.println("Le joueur 2 remporte la borne!");
                    borne.setProprietaire(2);
                    borne.setLocked(true);
                }
            }

            setGameEnded(plateau.gameended());

            piocherCarte(joueurCourant == 1 ? joueur1 : joueur2, 1);

            changerJoueurCourant();

            consoleView.afficherPlateau(plateau);

            System.out.println("----------------------------");
        }
        System.out.println("Le jeu est terminé! Le gagnant est le joueur " + plateau.getwinner());
    }

}