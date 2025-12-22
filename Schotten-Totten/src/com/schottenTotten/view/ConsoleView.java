package com.schottenTotten.view;
import java.util.Scanner;

import com.schottenTotten.model.*;

public class ConsoleView {
    
    private Scanner scanner;
    
    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }
    
    // ==================== MENU DE DEMARRAGE ====================
    
    public void afficherTitre() {
        System.out.println("========================================");
        System.out.println("         SCHOTTEN-TOTTEN");
        System.out.println("========================================");
        System.out.println();
    }
    
    public int afficherMenuVariante() {
        System.out.println("Choisissez une variante :");
        System.out.println("  1. Variante de Base");
        System.out.println("  2. Variante Tactique");
        System.out.print("Votre choix (1-2) : ");
        return lireEntier(1, 2);
    }
    
    public void afficherVarianteChoisie(String nomVariante) {
        System.out.println("Variante choisie : " + nomVariante);
        System.out.println();
    }
    
    public void afficherConfigJoueur(int numJoueur) {
        System.out.println("=== Configuration du Joueur " + numJoueur + " ===");
    }
    
    public String demanderNomJoueur(String nomParDefaut) {
        System.out.print("Nom du joueur (Entree pour \"" + nomParDefaut + "\") : ");
        String nom = scanner.nextLine().trim();
        return nom.isEmpty() ? nomParDefaut : nom;
    }
    
    public int demanderTypeJoueur() {
        System.out.println("Type de joueur :");
        System.out.println("  1. Humain");
        System.out.println("  2. IA (aleatoire)");
        System.out.print("Votre choix (1-2) : ");
        return lireEntier(1, 2);
    }
    
    public void afficherLancementPartie(String nomJ1, String nomJ2, String variante) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("Lancement de la partie...");
        System.out.println("  " + nomJ1 + " vs " + nomJ2);
        System.out.println("  Variante : " + variante);
        System.out.println("========================================");
        System.out.println();
    }
    
    private int lireEntier(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int valeur = Integer.parseInt(input);
                if (valeur >= min && valeur <= max) {
                    return valeur;
                }
                System.out.print("Entrez un nombre entre " + min + " et " + max + " : ");
            } catch (NumberFormatException e) {
                System.out.print("Entree invalide. Entrez un nombre : ");
            }
        }
    }
    
    // ==================== AFFICHAGE DU PLATEAU ====================
    
    public void afficherBorne(Borne borne, int index) {
        System.out.println(borne.getCartesJ1() + "  B-" + index + "    " + borne.getCartesJ2() +
                           (borne.isLocked() ? " (Locked, P:" + borne.getProprietaire() + ")" : ""));
    }

    public void afficherPlateau(Plateau plateau, int nombreBornes) {
        System.out.println("J1      Borne    J2");
        for (int i = 0; i < nombreBornes; i++) {
            afficherBorne(plateau.getBornes(i), i);
        }
    }

    public void afficherMainJoueur(Joueur joueur) {
        System.out.println(joueur.getName() + " Main: " + joueur.getHand());
    }
    
    // ==================== MESSAGES DU JEU ====================
    
    public void afficherMessage(String message) {
        System.out.println(message);
    }
    
    public void afficherSeparateur() {
        System.out.println("----------------------------");
    }
    
    public void afficherDeckVide() {
        System.out.println("Le deck est vide.");
    }
    
    public void afficherBornePleine() {
        System.out.println("Une borne est pleine, evaluation des mains...");
    }
    
    public void afficherGagnantBorne(Joueur joueur) {
        System.out.println("Le joueur " + joueur.getName() + " remporte la borne!");
    }

    public void afficherFinPartie(String gagnant) {
        System.out.println("Le jeu est termine! Le gagnant est le joueur " + gagnant);
    }
    
    // ==================== SAISIE UTILISATEUR ====================

    public Carte AskCarteFromPlayer(Joueur joueur, int index_final) {
        System.out.print("Choisissez une carte a jouer (index 0-" + (index_final - 1) + "): ");
        int index = scanner.nextInt();
        if (index < 0 || index >= index_final) {
            System.out.println("Index invalide. Veuillez reessayer.");
            return AskCarteFromPlayer(joueur, index_final);
        }
        return joueur.getHand().get(index);
    }

    public int choseBorneToPlay(Joueur joueur, int nbr_bornes) {
        System.out.print(joueur.getName() + ", choisissez une borne a jouer (index 0-" + (nbr_bornes - 1) + "): ");
        int index = scanner.nextInt();
        if (index < 0 || index >= nbr_bornes) {
            System.out.println("Index invalide. Veuillez reessayer.");
            return choseBorneToPlay(joueur, nbr_bornes);
        }
        return index;
    }

}