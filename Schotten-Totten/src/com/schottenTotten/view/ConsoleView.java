package com.schottenTotten.view;
import java.util.Scanner;

import com.schottenTotten.ai.IAAleatoire;
import com.schottenTotten.model.*;

public class ConsoleView {
    public void afficherBorne(Borne borne, int index) {
            System.out.println(borne.getCartesJ1() + "  B-" + index + "    " + borne.getCartesJ2() +
                               (borne.isLocked() ? " (Locked, P:" + borne.getProprietaire() +", type_1 : " + borne.TypeofHand(borne.getCartesJ1()) + ", type_2 : " +borne.TypeofHand(borne.getCartesJ2()) + ")" : ""));
    }

    public void afficherPlateau(Plateau plateau) {
        System.out.println("J1      Borne    J2");
        for (int i = 0; i < 9; i++) {
            afficherBorne(plateau.getBornes(i), i);
        }
    }


    public void afficherMainJoueur(Joueur joueur) {
        System.out.println(joueur.getName() + " Main: " + joueur.getHand());
    }

    private Carte choseCartefromPlayer(Joueur joueur) {
        if (joueur.getHand().isEmpty()) {
            throw new IllegalStateException("La main du joueur est vide.");
        }
        //input from terminal
        System.out.print("Choisissez une carte à jouer (index 0-" + (joueur.getHand().size() - 1) + "): ");
        Scanner scanner = new Scanner(System.in);
        int index = scanner.nextInt();
        if (index < 0 || index >= joueur.getHand().size()) {
            System.out.println("Index invalide. Veuillez réessayer.");
            return choseCarteToPlay(joueur); // re-ask if invalid
        }
        
        return joueur.getHand().get(index);
    }

    public  Carte choseCarteToPlay(Joueur joueur) {
        if (joueur.isAI()) {
            return IAAleatoire.choisirCarte(joueur);
        } else {
            return choseCartefromPlayer(joueur);
        }
    }

    public int choseBorneToPlay(Joueur joueur) {
        System.out.print(joueur.getName() + ", choisissez une borne à jouer (index 0-8): ");
        Scanner scanner = new Scanner(System.in);
        int index = scanner.nextInt();
        return index;
    }

}