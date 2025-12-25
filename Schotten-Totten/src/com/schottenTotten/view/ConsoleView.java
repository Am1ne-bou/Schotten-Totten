package com.schottenTotten.view;

import java.util.Scanner;
import java.util.List;
import com.schottenTotten.model.*;
import com.schottenTotten.model.carte.Carte;
import com.schottenTotten.model.carte.CarteClan;
import com.schottenTotten.model.carte.CarteTactique;
import com.schottenTotten.model.enums.Couleur;
import com.schottenTotten.utils.Constants;

import static com.schottenTotten.utils.Constants.*;

public class ConsoleView {
    private final Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    public void effacerEcran() {
        for (int i = 0; i < 50; i++) System.out.println();
    }

    public void attendreTouche(String message) {
        System.out.println();
        System.out.print(GRIS + message + RESET);
        scanner.nextLine();
    }

    // Mapping couleur
    // Move to color


    private int lireEntier(int min, int max) {
        // TO DO
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int valeur = Integer.parseInt(input);
                if (valeur >= min && valeur <= max) return valeur;
                System.out.print(ROUGE + "  Entrez un nombre entre " + min + " et " + max + " : " + RESET);
            } catch (NumberFormatException e) {
                System.out.print(ROUGE + "  Entree invalide. Entrez un nombre : " + RESET);
            }
        }
    }

    public void afficherTitre() {
        effacerEcran();
        System.out.println();
        System.out.println(CYAN + BOLD + "  +=========================================================+" + RESET);
        System.out.println(CYAN + BOLD + "  |                                                         |" + RESET);
        System.out.println(CYAN + BOLD + "  |                                                         |" + RESET);
        System.out.println(CYAN + BOLD + "  |" + JAUNE + "            S C H O T T E N - T O T T E N                " + CYAN + "|" + RESET);
        System.out.println(CYAN + BOLD + "  |                                                         |" + RESET);
        System.out.println(CYAN + BOLD + "  |                                                         |" + RESET);
        System.out.println(CYAN + BOLD + "  +=========================================================+" + RESET);
        System.out.println();
    }

    public int afficherMenuVariante() {
        System.out.println(BLANC + BOLD + "  +-----------------------------------+" + RESET);
        System.out.println(BLANC + BOLD + "  |     Choisissez une variante       |" + RESET);
        System.out.println(BLANC + BOLD + "  +-----------------------------------+" + RESET);
        System.out.println(BLANC + "  |  " + VERT + "1." + BLANC + " Variante de Base              |" + RESET);
        System.out.println(BLANC + "  |  " + MAGENTA + "2." + BLANC + " Variante Tactique             |" + RESET);
        System.out.println(BLANC + "  |  " + JAUNE + "3." + BLANC + " Variante Express              |" + RESET);
        System.out.println(BLANC + "  |  " + CYAN + "4." + BLANC + " Variante Express Tactique     |" + RESET);
        System.out.println(BLANC + BOLD + "  +-----------------------------------+" + RESET);
        System.out.print(CYAN + "  > Votre choix (1-4) : " + RESET);
        return lireEntier(1, 4);
    }

    public void afficherVarianteChoisie(String nomVariante) {
        System.out.println();
        System.out.println(VERT + "  [OK] " + nomVariante + " selectionnee!" + RESET);
        System.out.println();
    }

    public void afficherConfigJoueur(int numJoueur) {
        String couleur = (numJoueur == 1) ? BLEU : ROUGE;
        System.out.println();
        System.out.println(couleur + BOLD + "  ====== Configuration du Joueur " + numJoueur + " ======" + RESET);
    }

    public String demanderNomJoueur(String nomParDefaut) {
        System.out.print(CYAN + "  > Nom du joueur " + GRIS + "(Entree pour \"" + nomParDefaut + "\") " + CYAN + ": " + RESET);
        String nom = scanner.nextLine().trim();
        return nom.isEmpty() ? nomParDefaut : nom;
    }

    public int demanderTypeJoueur() {
        System.out.println(BLANC + "    Type de joueur :");
        System.out.println("      " + VERT + "1." + BLANC + " Humain");
        System.out.println("      " + MAGENTA + "2." + BLANC + " IA (aleatoire)" + RESET);
        System.out.print(CYAN + "  > Votre choix (1-2) : " + RESET);
        return lireEntier(1, 2);
    }

    public void afficherLancementPartie(String nomJ1, String nomJ2, String variante) {
        System.out.println();
        System.out.println(JAUNE + BOLD + "  +=========================================+" + RESET);
        System.out.println(JAUNE + BOLD + "  |        LANCEMENT DE LA PARTIE          |" + RESET);
        System.out.println(JAUNE + BOLD + "  +=========================================+" + RESET);
        System.out.println(JAUNE + "  |  " + BLEU + nomJ1 + JAUNE + " VS " + ROUGE + nomJ2 + RESET);
        System.out.println(JAUNE + "  |  " + GRIS + "Variante : " + variante + RESET);
        System.out.println(JAUNE + BOLD + "  +=========================================+" + RESET);
        System.out.println();
        attendreTouche("  Appuyez sur Entree pour commencer...");
    }

    private String getStatusBorne(Borne borne) {
        if (borne.isLocked()) {
            return (borne.getProprietaire() == 1) ? BLEU + BOLD + "J1" + RESET : ROUGE + BOLD + "J2" + RESET;
        }
        return GRIS + "--" + RESET;
    }

    private String formatCartePourBorne(Carte carte ) {
        if (carte == null) return GRIS + "[  ]" + RESET;
        if (carte.isTactique()) {
            CarteTactique carte_tac = (CarteTactique) carte;
            String typeorcolor = ( carte_tac.getCouleur() == null) ?  carte_tac.getType().toString().substring(1, 1) :  carte_tac.getCouleur().getAbreviation();
            String color = ( carte_tac.getCouleur() == null) ? CYAN : carte_tac.getCouleur().getCouleurCode(carte_tac.getCouleur());
            return color + BOLD + "[" +  carte.getValeur() + "-" + typeorcolor + "]" + RESET;
        }
        String couleurCode = carte.getCouleur().getCouleurCode(carte.getCouleur());
        return couleurCode + BOLD + "[" + carte.getValeur() + carte.getCouleur().toString().charAt(0) + "]" + RESET;
    }

    private void afficherLigne(int ligne, int joueur, Plateau plateau, int nombreBornes, int indexcombatdeboue) {
        if (ligne == 3) {
            System.out.print("    C" + (ligne + 1) + ":    ");
            for (int b = 0; b < nombreBornes; b++) {
                if (b == indexcombatdeboue) {
                    Borne borne = plateau.getBorne(b);
                    List<Carte> cartes = borne.getCartesParJoueur(joueur);
                    System.out.print((ligne < cartes.size()) ? " " + formatCartePourBorne(cartes.get(ligne)) + " " : GRIS + " [  ] " + RESET);
                } else {
                    System.out.print("      ");
                }

            }
            System.out.println();

        } else {
            System.out.print("    C" + (ligne + 1) + ":    ");
            for (int b = 0; b < nombreBornes; b++) {
                Borne borne = plateau.getBorne(b);
                List<Carte> cartes = borne.getCartesParJoueur(joueur);
                System.out.print((ligne < cartes.size()) ? " " + formatCartePourBorne(cartes.get(ligne)) + " " : GRIS + " [  ] " + RESET);
            }
            System.out.println();
        }
    }

    public void afficherPlateau(Plateau plateau, int nombreBornes) {
        System.out.println();
        System.out.println(CYAN + BOLD + "  ======================= PLATEAU DE JEU =======================" + RESET);
        System.out.println();

        // Ligne des numéros de bornes
        System.out.print(JAUNE + "  BORNE:   " + RESET);
        for (int i = 0; i < nombreBornes; i++) System.out.print(JAUNE + BOLD + "  B" + i + "  " + RESET);
        System.out.println();
        System.out.println(CYAN + "  --------------------------------------------------------------" + RESET);

        int nbrLignes = 3;

        // borne combat de boue
        int IndexCombatdeBoue = plateau.getIndexCombatDeBoue(nombreBornes);
        if (IndexCombatdeBoue != -1) nbrLignes = 4;


        // Cartes du Joueur 1
        System.out.println(BLEU + BOLD + "  JOUEUR 1:" + RESET);
        for (int ligne = 0; ligne < nbrLignes; ligne++) {
            afficherLigne(ligne, 1, plateau, nombreBornes, IndexCombatdeBoue);
        }

        System.out.println(CYAN + "  --------------------------------------------------------------" + RESET);

        // Status des bornes
        System.out.print(JAUNE + BOLD + "  STATUS:  " + RESET);
        for (int i = 0; i < nombreBornes; i++) {
            Borne borne = plateau.getBorne(i);
            String status = getStatusBorne(borne);
            String mod = borne.hasColinMaillard() ? MAGENTA + "CM" + RESET : (borne.hasCombatDeBoue() ? ORANGE + "CB" + RESET : "  ");
            System.out.print(" " + status + mod + " ");
        }
        System.out.println();

        System.out.println(CYAN + "  --------------------------------------------------------------" + RESET);

        // Cartes du Joueur 2
        System.out.println(ROUGE + BOLD + "  JOUEUR 2:" + RESET);
        for (int ligne = 0; ligne < nbrLignes; ligne++) {
            afficherLigne(ligne, 2, plateau, nombreBornes, IndexCombatdeBoue);
        }

        System.out.println(CYAN + BOLD + "  ==============================================================" + RESET);
        System.out.println();
    }

    public void afficherDeckInfo(int cartesRestantes, int cartesTactiquesRestantes) {
        System.out.println(MAGENTA + "  +----------------------------+" + RESET);
        System.out.println(MAGENTA + "  |          DECKS             |" + RESET);
        System.out.println(MAGENTA + "  |  Cartes Clan    : " + BLANC + BOLD + String.format("%2d", cartesRestantes) + MAGENTA + "       |" + RESET);
        if (cartesTactiquesRestantes >= 0) {
            System.out.println(MAGENTA + "  |  Cartes Tactiques: " + BLANC + BOLD + String.format("%2d", cartesTactiquesRestantes) + MAGENTA + "      |" + RESET);
        }
        System.out.println(MAGENTA + "  +----------------------------+" + RESET);
    }

    public void afficherMainJoueur(Joueur joueur) {
        String couleur = VERT;
        System.out.println();
        System.out.println(couleur + BOLD + "  +----------------------------------------------------------------+" + RESET);
        System.out.println(couleur + BOLD + "  |  Main de " + joueur.getName() + RESET);
        System.out.println(couleur + BOLD + "  +----------------------------------------------------------------+" + RESET);
        System.out.print(couleur + "  |  " + RESET);

        List<Carte> main = joueur.getHand();
        for (int i = 0; i < main.size(); i++) {
            Carte carte = main.get(i);
            String carteFormatee = (carte.getCouleur() != null) ? carte.getCouleur().colorerCarte(carte) :  CYAN + BOLD + "[" + carte.getValeur() + "-" + ((CarteTactique) carte).getType().toString().substring(0, 5) + "]" + RESET;
            System.out.print(GRIS + "[" + i + "]" + RESET + carteFormatee + "  ");
        }
        System.out.println();
        System.out.println(couleur + BOLD + "  +----------------------------------------------------------------+" + RESET);
    }

    public void afficherTransition(String nomJoueurSuivant) {
        effacerEcran();
        System.out.println();
        System.out.println();
        System.out.println(JAUNE + BOLD + "  +=========================================================+" + RESET);
        System.out.println(JAUNE + BOLD + "  |                                                         |" + RESET);
        System.out.println(JAUNE + BOLD + "  |              CHANGEMENT DE JOUEUR                       |" + RESET);
        System.out.println(JAUNE + BOLD + "  |                                                         |" + RESET);
        System.out.println(JAUNE + BOLD + "  |  Passez l'ecran a : " + CYAN + nomJoueurSuivant + JAUNE + "                         |" + RESET);
        System.out.println(JAUNE + BOLD + "  |                                                         |" + RESET);
        System.out.println(JAUNE + BOLD + "  +=========================================================+" + RESET);
        System.out.println();
        attendreTouche("  " + nomJoueurSuivant + ", appuyez sur Entree quand vous etes pret...");
        effacerEcran();
    }

    public void afficherMessage(String message) {
        System.out.println(BLANC + "  [MSG] " + message + RESET);
    }

    public void afficherSeparateur() {
        System.out.println(GRIS + "  ----------------------------------------" + RESET);
    }

    public void afficherDeckVide() {
        System.out.println(ORANGE + "  [!] Le deck est vide!" + RESET);
    }

    public void afficherGagnantBorne(int joueur, int indexBorne) {
        String couleur = (joueur == 1) ? BLEU : ROUGE;
        System.out.println();
        System.out.println(couleur + BOLD + "  >>> Le Joueur " + joueur + " remporte la Borne " + indexBorne + " ! <<<" + RESET);
        System.out.println();
    }

    public void afficherCartesComparees(List<Carte> cartesJ1, List<Carte> cartesJ2, int typeJ1, int typeJ2) {
        System.out.println(GRIS + "  Comparaison:" + RESET);
        System.out.println(BLEU + "    J1: " + RESET + Couleur.BLEU.colorerCartes(cartesJ1) + GRIS + " -> " + getNomCombinaison(typeJ1) + RESET);
        System.out.println(ROUGE + "    J2: " + RESET + Couleur.ROUGE.colorerCartes(cartesJ2) + GRIS + " -> " + getNomCombinaison(typeJ2) + RESET);
    }

    private String getNomCombinaison(int type) {
        switch (type) {
            case 4:
                return JAUNE + BOLD + "Suite Couleur!" + RESET;
            case 3:
                return MAGENTA + "Brelan" + RESET;
            case 2:
                return CYAN + "Couleur" + RESET;
            case 1:
                return VERT + "Suite" + RESET;
            default:
                return GRIS + "Somme" + RESET;
        }
    }

    public void afficherFinPartie(int gagnant, String nomGagnant) {
        String couleur = (gagnant == 1) ? BLEU : ROUGE;

        // Calculer la largeur nécessaire pour le nom du gagnant
        String texteVainqueur = "VAINQUEUR : " + nomGagnant;
        int largeurMinimale = Math.max(
                texteVainqueur.length() + 10,  // +10 pour les marges
                "FIN DE LA PARTIE !".length() + 10
        );

        // Arrondir à un multiple de 2 pour garder la symétrie
        int largeur = (largeurMinimale % 2 == 0) ? largeurMinimale : largeurMinimale + 1;

        // Construction du tableau
        String bordure = "  +" + "=".repeat(largeur) + "+";
        String ligneVide = "  |" + " ".repeat(largeur) + "|";

        System.out.println();
        System.out.println();
        System.out.println(JAUNE + BOLD + bordure + RESET);
        System.out.println(JAUNE + BOLD + ligneVide + RESET);
        System.out.println(JAUNE + BOLD + "  |" + centrer("FIN DE LA PARTIE !", largeur) + "|" + RESET);
        System.out.println(JAUNE + BOLD + ligneVide + RESET);

        // Ligne du vainqueur avec couleur
        String ligneVainqueur = centrer(texteVainqueur, largeur);
        ligneVainqueur = ligneVainqueur.replace(nomGagnant, couleur + nomGagnant + JAUNE);
        System.out.println(JAUNE + BOLD + "  |" + ligneVainqueur + "|" + RESET);

        System.out.println(JAUNE + BOLD + ligneVide + RESET);
        System.out.println(JAUNE + BOLD + "  |" + centrer("Félicitations !", largeur) + "|" + RESET);
        System.out.println(JAUNE + BOLD + ligneVide + RESET);
        System.out.println(JAUNE + BOLD + bordure + RESET);
        System.out.println();
    }

    /**
     * Centre un texte dans une largeur donnée
     */
    private String centrer(String texte, int largeur) {
        int espacesTotal = largeur - texte.length();
        int espacesGauche = espacesTotal / 2;
        int espacesDroite = espacesTotal - espacesGauche;
        return " ".repeat(espacesGauche) + texte + " ".repeat(espacesDroite);
    }

    public void afficherScoreFinal(int bornesJ1, int bornesJ2, String nomJ1, String nomJ2) {
        System.out.println(CYAN + "  +-----------------------------------+" + RESET);
        System.out.println(CYAN + "  |         SCORE FINAL              |" + RESET);
        System.out.println(CYAN + "  +-----------------------------------+" + RESET);
        System.out.println(CYAN + "  |  " + BLEU + nomJ1 + ": " + bornesJ1 + " bornes" + CYAN + "              |" + RESET);
        System.out.println(CYAN + "  |  " + ROUGE + nomJ2 + ": " + bornesJ2 + " bornes" + CYAN + "              |" + RESET);
        System.out.println(CYAN + "  +-----------------------------------+" + RESET);
    }

    public int demanderRejouer() {
        System.out.print(VERT + " Voulez vous rejouer? OUI (1) Non (2) : " + RESET);
        return lireEntier(1, 2);
    }

    public Carte AskCarteFromPlayer(Joueur joueur) {
        System.out.print(CYAN + "  > Choisissez une carte (0-" + (joueur.getHandSize() - 1) + ") : " + RESET);
        int index = lireEntier(0, joueur.getHandSize() - 1);
        return joueur.getHand().get(index);
    }

    public int choseBorneToPlay(Joueur joueur, int nbr_bornes) {
        System.out.print(CYAN + "  > Choisissez une borne (0-" + (nbr_bornes - 1) + ") : " + RESET);
        return lireEntier(0, nbr_bornes - 1);
    }

    public void afficherCarteJouee(String nomJoueur, Carte carte, int indexBorne) {
        System.out.println();
        System.out.println(VERT + "  [OK] " + nomJoueur + " joue " + carte.getCouleur().colorerCarte(carte) + VERT + " sur la Borne " + indexBorne + RESET);
    }

    public int demanderTypeCarteAJouer(boolean aCartesClan, boolean peutJouerTactique) {
        System.out.println();
        System.out.println(BLANC + BOLD + "  +-----------------------------------+" + RESET);
        System.out.println(BLANC + BOLD + "  |    Que voulez-vous jouer ?       |" + RESET);
        System.out.println(BLANC + BOLD + "  +-----------------------------------+" + RESET);
        if (aCartesClan)
            System.out.println(BLANC + "  |  " + VERT + "1." + BLANC + " Carte Clan                    |" + RESET);
        if (peutJouerTactique)
            System.out.println(BLANC + "  |  " + MAGENTA + "2." + BLANC + " Carte Tactique                |" + RESET);
        System.out.println(BLANC + "  |  " + GRIS + "0." + BLANC + " Passer                        |" + RESET);
        System.out.println(BLANC + BOLD + "  +-----------------------------------+" + RESET);
        System.out.print(CYAN + "  > Votre choix : " + RESET);
        return lireEntier(0, peutJouerTactique ? 2 : 1);
    }

    public int demanderChoixPioche(boolean clanDispo, boolean tactiqueDispo) {
        System.out.println();
        System.out.println(BLANC + BOLD + "  +-----------------------------------+" + RESET);
        System.out.println(BLANC + BOLD + "  |    Piocher une carte             |" + RESET);
        System.out.println(BLANC + BOLD + "  +-----------------------------------+" + RESET);
        if (clanDispo)
            System.out.println(BLANC + "  |  " + VERT + "1." + BLANC + " Carte Clan                    |" + RESET);
        if (tactiqueDispo)
            System.out.println(BLANC + "  |  " + MAGENTA + "2." + BLANC + " Carte Tactique                |" + RESET);
        System.out.println(BLANC + BOLD + "  +-----------------------------------+" + RESET);
        System.out.print(CYAN + "  > Votre choix : " + RESET);
        return lireEntier(clanDispo ? 1 : 2, tactiqueDispo ? 2 : 1);
    }

    public Couleur demanderCouleur() {
        System.out.println();
        System.out.println(BLANC + BOLD + "  +-----------------------------------+" + RESET);
        System.out.println(BLANC + BOLD + "  |    Choisissez une couleur        |" + RESET);
        System.out.println(BLANC + BOLD + "  +-----------------------------------+" + RESET);
        Couleur[] couleurs = Couleur.values();
        for (int i = 0; i < couleurs.length; i++) {
            String couleurCode = couleurs[i].getCouleurCode(couleurs[i]);
            System.out.println(BLANC + "  |  " + couleurCode + i + "." + BLANC + " " + couleurs[i] + "                         |" + RESET);
        }
        System.out.println(BLANC + BOLD + "  +-----------------------------------+" + RESET);
        System.out.print(CYAN + "  > Votre choix : " + RESET);
        return couleurs[lireEntier(0, couleurs.length - 1)];
    }

    public int demanderValeur(int min, int max) {
        System.out.print(CYAN + "  > Choisissez une valeur (" + min + "-" + max + ") : " + RESET);
        return lireEntier(min, max);
    }

    public int demanderIndex(String message, int max) {
        System.out.print(CYAN + "  > " + message + " (0-" + (max - 1) + ") : " + RESET);
        return lireEntier(0, max - 1);
    }

    public int demanderChoixStratege() {
        System.out.println();
        System.out.println(BLANC + BOLD + "  +-----------------------------------+" + RESET);
        System.out.println(BLANC + BOLD + "  |    Action du Stratege            |" + RESET);
        System.out.println(BLANC + BOLD + "  +-----------------------------------+" + RESET);
        System.out.println(BLANC + "  |  " + VERT + "1." + BLANC + " Deplacer vers autre borne     |" + RESET);
        System.out.println(BLANC + "  |  " + ROUGE + "2." + BLANC + " Defausser la carte            |" + RESET);
        System.out.println(BLANC + BOLD + "  +-----------------------------------+" + RESET);
        System.out.print(CYAN + "  > Votre choix : " + RESET);
        return lireEntier(1, 2);
    }

    public void afficherCartesTactiques(List<CarteTactique> cartes) {
        System.out.println();
        System.out.println(MAGENTA + BOLD + "  +----------------------------------------------------------------+" + RESET);
        System.out.println(MAGENTA + BOLD + "  |  Cartes Tactiques disponibles                                 |" + RESET);
        System.out.println(MAGENTA + BOLD + "  +----------------------------------------------------------------+" + RESET);
        for (int i = 0; i < cartes.size(); i++) {
            CarteTactique ct = cartes.get(i);
            System.out.println(MAGENTA + "  |  " + GRIS + "[" + i + "] " + MAGENTA + BOLD + ct.getType().getNom() + RESET + GRIS + " - " + ct.getType().getDescription() + RESET);
        }
        System.out.println(MAGENTA + BOLD + "  +----------------------------------------------------------------+" + RESET);
    }

    public void afficherInfoTactique(int tactiquesJ1, int tactiquesJ2) {
        System.out.println(GRIS + "  Cartes tactiques jouees: " + BLEU + "J1=" + tactiquesJ1 + GRIS + " | " + ROUGE + "J2=" + tactiquesJ2 + RESET);
    }

    public void afficherModeCombatApplique(String nomMode, int indexBorne) {
        System.out.println();
        System.out.println(ORANGE + BOLD + "  [!] " + nomMode + " applique a la Borne " + indexBorne + " !" + RESET);
    }

    public int demanderBorne(Joueur joueur, int nombreBornes) {
        return demanderIndex("Choisissez une borne", nombreBornes);
    }
}