package com.schottenTotten.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.schottenTotten.ai.IAAleatoire;
import com.schottenTotten.model.*;
import com.schottenTotten.model.carte.Carte;
import com.schottenTotten.model.carte.CarteClan;
import com.schottenTotten.model.carte.CarteTactique;
import com.schottenTotten.model.decks.Deck;
import com.schottenTotten.model.decks.DeckClan;
import com.schottenTotten.model.decks.DeckTactique;
import com.schottenTotten.model.enums.Couleur;
import com.schottenTotten.model.enums.TypeCarteTactique;
import com.schottenTotten.utils.Constants;
import com.schottenTotten.view.ConsoleView;

public class Jeu {
    private final Plateau plateau;
    private final DeckClan deckClan;
    private DeckTactique deckTactique;
    private final Joueur joueur1;
    private final Joueur joueur2;
    private final VarianteJeu variante;
    private int joueurCourant;
    private boolean gameEnded;
    private final ConsoleView view;
    private GestionTactique gestionTactique;
    private int toursPassesConsecutifs;
    private static final int MAX_TOURS_PASSES = 4;

    public Jeu(VarianteJeu variante, Joueur joueur1, Joueur joueur2) {
        this.variante = variante;
        this.plateau = new Plateau(variante.getNombreBornes());
        this.deckClan = new DeckClan();
        this.deckClan.shuffle();
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.joueurCourant = 1;
        this.gameEnded = false;
        this.view = new ConsoleView();
        this.toursPassesConsecutifs = 0;

        if (variante.hasCartesTactiques()) {
            this.deckTactique = new DeckTactique();
            this.deckTactique.shuffle();
            this.gestionTactique = new GestionTactique(this, view);
        }
    }

    public Jeu() {
        this.variante = VarianteJeu.BASE;
        this.plateau = new Plateau(variante.getNombreBornes());
        this.deckClan = new DeckClan();
        this.deckClan.shuffle();
        this.joueur1 = new Joueur("nomJ1", true);
        this.joueur2 = new Joueur("nomJ2", true);
        this.joueurCourant = 1;
        this.gameEnded = false;
        this.view = new ConsoleView();
        this.toursPassesConsecutifs = 0;


        if (variante.hasCartesTactiques()) {
            this.deckTactique = new DeckTactique();
            this.deckTactique.shuffle();
            this.gestionTactique = new GestionTactique(this, view);
        }
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public Deck getDeckClan() {
        return deckClan;
    }

    public DeckTactique getDeckTactique() {
        return deckTactique;
    }

    public int getJoueurCourant() {
        return joueurCourant;
    }

    public VarianteJeu getVariante() {
        return variante;
    }

    // Getter unifié pour récupérer un joueur par numéro
    public Joueur getJoueur(int num) {
        return num == 1 ? joueur1 : joueur2;
    }

    private Joueur getJoueurActuel() {
        return getJoueur(joueurCourant);
    }

    private Joueur getJoueurAdverse() {
        return getJoueur(joueurCourant == 1 ? 2 : 1);
    }

    private void incrementerTourPasse() {
        toursPassesConsecutifs++;
    }

    private void reinitialiserToursPasses() {
        toursPassesConsecutifs = 0;
    }

    private boolean jeuBloqueParToursPasses() {
        return toursPassesConsecutifs >= MAX_TOURS_PASSES;
    }

    public boolean peutAjouterCarte(Borne borne, int joueur) {
        if (borne.isLocked()) return false;
        int max = borne.hasCombatDeBoue() ? 4 : 3;
        return borne.getNbCartes(joueur) < max;
    }

    public void ajouterCarte(Borne borne, int joueur, Carte carte) {
        borne.addCarteToJoueurBorne(joueur, carte);
    }

    public Carte retirerCarte(Borne borne, int joueur, int index) {
        return borne.removeCarte(joueur, index);
    }

    private boolean isBorneFull(Borne borne) {
        int required = borne.hasCombatDeBoue() ? 4 : 3;
        return borne.getNbCartes(1) >= required && borne.getNbCartes(2) >= required;
    }

    private int getTypeCombinaison(List<Carte> cartes) {
        if (cartes.size() < 3) return 0;

        List<Integer> valeurs = new ArrayList<>();
        List<Couleur> couleurs = new ArrayList<>();
        for (Carte carte : cartes) {
            valeurs.add(carte.getValeur());
            couleurs.add(carte.getCouleur());
        }
        Collections.sort(valeurs);

        boolean isSuite = true;
        for (int i = 1; i < valeurs.size(); i++) {
            if (valeurs.get(i) - valeurs.get(i - 1) != 1) {
                isSuite = false;
                break;
            }
        }
        boolean isCouleur = couleurs.stream().allMatch(c -> c != null && c.equals(couleurs.getFirst()));
        boolean isBrelan = valeurs.stream().distinct().count() == 1;

        if (isSuite && isCouleur) return 4;
        if (isBrelan) return 3;
        if (isCouleur) return 2;
        if (isSuite) return 1;
        return 0;
    }

    private int getSommeCartes(List<Carte> cartes) {
        int somme = 0;
        for (Carte carte : cartes) somme += carte.getValeur();
        return somme;
    }

    private int comparerBorne(Borne borne) {
        List<Carte> cartesJ1 = borne.getCartesJ1();
        List<Carte> cartesJ2 = borne.getCartesJ2();

        // Colin-Maillard : seule la somme compte
        if (borne.hasColinMaillard()) {
            int sommeJ1 = getSommeCartes(cartesJ1);
            int sommeJ2 = getSommeCartes(cartesJ2);
            if (sommeJ1 > sommeJ2) return 1;
            if (sommeJ2 > sommeJ1) return 2;
            return borne.getLastPlayer();
        }

        int typeJ1 = getTypeCombinaison(cartesJ1);
        int typeJ2 = getTypeCombinaison(cartesJ2);
        view.afficherCartesComparees(cartesJ1, cartesJ2, typeJ1, typeJ2);

        if (typeJ1 > typeJ2) return 1;
        if (typeJ2 > typeJ1) return 2;

        int sommeJ1 = getSommeCartes(cartesJ1);
        int sommeJ2 = getSommeCartes(cartesJ2);
        if (sommeJ1 > sommeJ2) return 1;
        if (sommeJ2 > sommeJ1) return 2;
        return borne.getLastPlayer();
    }

    private int getNombreBornesControlees(int joueur) {
        int count = 0;
        for (int i = 0; i < variante.getNombreBornes(); i++) {
            if (plateau.getBorne(i).getProprietaire() == joueur) count++;
        }
        return count;
    }

    private boolean aGagne(int joueur) {
        if (getNombreBornesControlees(joueur) >= variante.getBornesToWin()) return true;
        int suite = 0;
        for (int i = 0; i < variante.getNombreBornes(); i++) {
            if (plateau.getBorne(i).getProprietaire() == joueur) {
                suite++;
                if (suite >= variante.getSuiteBornes()) return true;
            } else {
                suite = 0;
            }
        }
        return false;
    }

    private void distribuerCartesInitiales() {
        for (int i = 0; i < variante.getCartesInitiales(); i++) {
            joueur1.addCartesToHand(deckClan.piocher(1));
            joueur2.addCartesToHand(deckClan.piocher(1));
        }
    }

    // Pioche unifiée : type 1=clan, type 2=tactique
    private void piocher(Joueur joueur, int type) {
        List<Carte> carte;
        if (type == 1) {
            carte = deckClan.piocher(1);
            if (carte.isEmpty()) {
                view.afficherDeckVide();
                return;
            }
        } else {
            if (deckTactique == null || deckTactique.isEmpty()) return;
            carte = deckTactique.piocher(1);
        }
        joueur.addCartesToHand(carte);
    }

    private void changerJoueurCourant() {
        joueurCourant = (joueurCourant % 2) + 1;
    }

    private int trouverBorneAEvaluer() {
        for (int i = 0; i < variante.getNombreBornes(); i++) {
            Borne borne = plateau.getBorne(i);
            if (isBorneFull(borne) && !borne.isLocked()) return i;
        }
        return -1;
    }

    private void evaluerBorne(int indexBorne) {
        Borne borne = plateau.getBorne(indexBorne);
        int gagnant = comparerBorne(borne);
        borne.setProprietaire(gagnant);
        borne.setLocked(true);
        view.afficherGagnantBorne(gagnant, indexBorne);
    }

    private void jouerCarteClan(Joueur joueur) {
        List<CarteClan> cartesClan = joueur.getCartesClan();
        if (cartesClan.isEmpty()) {
            view.afficherMessage("Aucune carte clan disponible!");
            incrementerTourPasse();
            return;
        }

        Carte carte = view.AskCarteFromPlayer(joueur);
        while (carte.isTactique()) {
            view.afficherMessage("Choisissez une carte CLAN, pas tactique.");
            carte = view.AskCarteFromPlayer(joueur);
        }

        int indexBorne = view.choseBorneToPlay(joueur, variante.getNombreBornes());
        Borne borne = plateau.getBorne(indexBorne);
        while (!peutAjouterCarte(borne, joueurCourant)) {
            view.afficherMessage("Borne invalide. Réessayez.");
            indexBorne = view.choseBorneToPlay(joueur, variante.getNombreBornes());
            borne = plateau.getBorne(indexBorne);
        }

        ajouterCarte(borne, joueurCourant, carte);
        joueur.removeCarteFromHand(carte);
        view.afficherCarteJouee(joueur.getName(), carte, indexBorne);
        reinitialiserToursPasses();
    }

    private void jouerCarteTactique(Joueur joueur) {
        if (gestionTactique == null) return;

        List<CarteTactique> cartesTactiques = joueur.getCartesTactiques();
        view.afficherCartesTactiques(cartesTactiques);

        int indexCarte = view.demanderIndex("Choisissez une carte tactique", cartesTactiques.size());
        CarteTactique carte = cartesTactiques.get(indexCarte);

        boolean succes = gestionTactique.executerCarteTactique(carte, joueur, joueurCourant);
        if (succes) {
            joueur.removeCarteFromHand(carte);
            joueur.incrementerCartesTactiquesJouees();
            reinitialiserToursPasses();
        } else {
            incrementerTourPasse();
        }
    }

    private void jouerCarteIA(Joueur joueur) {
        List<CarteClan> cartesClan = joueur.getCartesClan();
        if (cartesClan.isEmpty()) {
            view.afficherMessage(joueur.getName() + " passe son tour.");
            incrementerTourPasse();
            return;
        }

        CarteClan carteClan = IAAleatoire.choisirCarte(joueur, cartesClan);
        int indexBorne = IAAleatoire.choisirBorne(this);
        Borne borne = plateau.getBorne(indexBorne);

        ajouterCarte(borne, joueurCourant, carteClan);
        joueur.removeCarteFromHand(carteClan);
        view.afficherCarteJouee(joueur.getName(), carteClan, indexBorne);
        reinitialiserToursPasses();
    }

    private void jouerCarteTactiqueIA(Joueur joueur) {
        List<CarteTactique> cartesTactiques = joueur.getCartesTactiques();
        if (cartesTactiques.isEmpty()) return;

        CarteTactique carte = IAAleatoire.choisirCarteTactique(cartesTactiques);
        assert carte != null;
        TypeCarteTactique type = carte.getType();

        // Troupes d'élite : configurer et placer sur une borne
        if (type.getCategorie() == TypeCarteTactique.TypeCategorie.TROUPE_ELITE) {
            Couleur couleur = IAAleatoire.choisirCouleur();
            int valeur;
            switch (type) {
                case JOKER:
                    valeur = IAAleatoire.choisirValeur(1, 9);
                    break;
                case ESPION:
                    valeur = 7;
                    break;
                case PORTE_BOUCLIER:
                    valeur = IAAleatoire.choisirValeur(1, 3);
                    break;
                default:
                    incrementerTourPasse();
                    return;
            }
            carte.setCouleur(couleur);
            carte.setValeur(valeur);

            int indexBorne = IAAleatoire.choisirBorne(this);
            Borne borne = plateau.getBorne(indexBorne);
            ajouterCarte(borne, joueurCourant, carte);
            joueur.removeCarteFromHand(carte);
            joueur.incrementerCartesTactiquesJouees();
            view.afficherMessage(joueur.getName() + " joue " + carte + " sur la borne " + indexBorne);
            reinitialiserToursPasses();
        }
        // Modes de combat : appliquer sur une borne non revendiquée
        else if (type.getCategorie() == TypeCarteTactique.TypeCategorie.MODE_COMBAT) {
            int indexBorne = IAAleatoire.choisirBorneNonRevendiquee(this);
            Borne borne = plateau.getBorne(indexBorne);
            if (type == TypeCarteTactique.COLIN_MAILLARD) borne.setColinMaillard(true);
            else if (type == TypeCarteTactique.COMBAT_DE_BOUE) borne.setCombatDeBoue(true);
            joueur.removeCarteFromHand(carte);
            joueur.incrementerCartesTactiquesJouees();
            view.afficherMessage(joueur.getName() + " joue " + type.getNom() + " sur la borne " + indexBorne);
            reinitialiserToursPasses();
        }
        // Ruses : l'IA passe
//        else if (type.getCategorie() == TypeCarteTactique.TypeCategorie.RUSE) {
//            List<CarteClan> cartesClan = joueur.getCartesClan();
//            if (!cartesClan.isEmpty()) {
//                jouerCarteIA(joueur);
//            } else {
//                view.afficherMessage(joueur.getName() + " passe son tour.");
//            }
//        }

        else if (type.getCategorie() == TypeCarteTactique.TypeCategorie.RUSE) {
            boolean carteJouee = false;
            switch (type) {
                case CHASSEUR_DE_TETE:
                    executerChasseurDeTeteAI(joueur);
                    carteJouee = true;
                    break;
                case STRATEGIE:
                    executerStrategeIA(joueurCourant);
                    carteJouee = true;
                    break;
                case BANSHEE:
                    executerBansheeIA((joueurCourant % 2) + 1);
                    carteJouee = true;
                    break;
                case TRAITRE:
                    executerTraitreIA(joueurCourant, (joueurCourant % 2) + 1);
                    carteJouee = true;
                    break;
                default:
                    List<CarteClan> cartesClan = joueur.getCartesClan();
                    if (!cartesClan.isEmpty()) {
                        jouerCarteIA(joueur);
                    } else {
                        view.afficherMessage(joueur.getName() + " passe son tour.");
                        incrementerTourPasse();
                    }
                    break;
            }

            // Retirer la carte et incrémenter si elle a été jouée avec succès
            if (carteJouee) {
                joueur.removeCarteFromHand(carte);
                joueur.incrementerCartesTactiquesJouees();
                reinitialiserToursPasses();
            }
        }
    }

    private void executerChasseurDeTeteAI(Joueur joueur) {
        view.afficherMessage("=== Chasseur de Tête ===");
        view.afficherMessage("Piochez 3 cartes, gardez-en 2.");

        List<Carte> piochees = new ArrayList<>();
        Deck deck = getDeckClan();
        DeckTactique deckTactique = getDeckTactique();

        for (int i = 0; i < 3; i++) {
            int choix = IAAleatoire.choisirTypePioche(!deck.isEmpty(), deckTactique != null && !deckTactique.isEmpty());
            if (choix == 1 && !deck.isEmpty()) {
                piochees.addAll(deck.piocher(1));
            } else if (choix == 2 && deckTactique != null && !deckTactique.isEmpty()) {
                piochees.addAll(deckTactique.piocher(1));
            }
        }

        if (piochees.isEmpty()) {
            view.afficherMessage("Aucune carte piochée!");
            return;
        }

        view.afficherMessage("Cartes piochées:");
        for (int i = 0; i < piochees.size(); i++) {
            view.afficherMessage(i + ": " + piochees.get(i));
        }

        int indexDefausse = IAAleatoire.choisirIndex(piochees.size());
        piochees.remove(indexDefausse);

        for (Carte c : piochees) joueur.addCartesToHand(Collections.singletonList(c));
        view.afficherMessage("Cartes ajoutées à votre main.");
    }

    private void executerStrategeIA(int joueurNum) {
        view.afficherMessage("=== Stratège ===");
        Plateau plateau = getPlateau();
        int nombreBornes = getVariante().getNombreBornes();

        int borneSource = IAAleatoire.choisirBorne(this);
        Borne source = plateau.getBorne(borneSource);
        List<Carte> carteClans = source.getCartesParJoueur(joueurNum);

        if (carteClans.isEmpty()) {
            view.afficherMessage("Aucune carte sur cette borne!");
            return;
        }

        view.afficherMessage("Cartes:");
        for (int i = 0; i < carteClans.size(); i++) {
            view.afficherMessage(i + ": " + carteClans.get(i));
        }

        int indexCarte = IAAleatoire.choisirIndex(carteClans.size());
        int action = IAAleatoire.choisirValeur(1, 2);

        if (action == 1) {
            int borneDest = IAAleatoire.choisirIndex(nombreBornes);
            Borne dest = plateau.getBorne(borneDest);

            if (!peutAjouterCarte(dest, joueurNum)) {
                view.afficherMessage("Destination invalide!");
                return;
            }

            Carte carteClan = retirerCarte(source, joueurNum, indexCarte);
            ajouterCarte(dest, joueurNum, carteClan);
            view.afficherMessage("Carte déplacée.");
        } else {
            retirerCarte(source, joueurNum, indexCarte);
            view.afficherMessage("Carte défaussée.");
        }
    }

    private List<Integer> getIndicesCartesClan(List<Carte> cartes) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < cartes.size(); i++) {
            if (!(cartes.get(i).isTactique())) indices.add(i);
        }
        return indices;
    }

    private void executerBansheeIA(int adversaireNum) {
        view.afficherMessage("=== Banshee ===");
        Plateau plateau = getPlateau();
        int nombreBornes = getVariante().getNombreBornes();

        int indexBorne = IAAleatoire.choisirIndex(nombreBornes);
        Borne borne = plateau.getBorne(indexBorne);

        if (borne.isLocked()) {
            view.afficherMessage("Borne déjà revendiquée!");
            return;
        }

        List<Carte> cartesAdversaire = borne.getCartesParJoueur(adversaireNum);
        if (cartesAdversaire.isEmpty()) {
            view.afficherMessage("Aucune carte adverse!");
            return;
        }

        List<Integer> indicesValides = getIndicesCartesClan(cartesAdversaire);
        if (indicesValides.isEmpty()) {
            view.afficherMessage("Aucune carte clan à défausser!");
            return;
        }

        view.afficherMessage("Cartes adverses (clan uniquement):");
        for (int i = 0; i < indicesValides.size(); i++) {
            view.afficherMessage(i + ": " + cartesAdversaire.get(indicesValides.get(i)));
        }

        int choix = IAAleatoire.choisirIndex(indicesValides.size());
        retirerCarte(borne, adversaireNum, indicesValides.get(choix));
        view.afficherMessage("Carte défaussée!");
    }

    private void executerTraitreIA(int joueurNum, int adversaireNum) {
        view.afficherMessage("=== Traître ===");
        Plateau plateau = getPlateau();
        int nombreBornes = getVariante().getNombreBornes();

        int borneSource = IAAleatoire.choisirIndex(nombreBornes);
        Borne source = plateau.getBorne(borneSource);

        if (source.isLocked()) {
            view.afficherMessage("Borne déjà revendiquée!");
            return;
        }

        List<Carte> cartesAdversaire = source.getCartesParJoueur(adversaireNum);
        if (cartesAdversaire.isEmpty()) {
            view.afficherMessage("Aucune carte adverse!");
            return;
        }

        List<Integer> indicesValides = getIndicesCartesClan(cartesAdversaire);
        if (indicesValides.isEmpty()) {
            view.afficherMessage("Aucune carte clan à voler!");
            return;
        }

        view.afficherMessage("Cartes adverses (clan uniquement):");
        for (int i = 0; i < indicesValides.size(); i++) {
            view.afficherMessage(i + ": " + cartesAdversaire.get(indicesValides.get(i)));
        }

        int choix = IAAleatoire.choisirIndex(indicesValides.size());
        Carte carteClan = retirerCarte(source, adversaireNum, indicesValides.get(choix));

        int borneDest = IAAleatoire.choisirIndex(nombreBornes);
        Borne dest = plateau.getBorne(borneDest);

        if (!peutAjouterCarte(dest, joueurNum)) {
            view.afficherMessage("Destination invalide!");
            return;
        }

        ajouterCarte(dest, joueurNum, carteClan);
        view.afficherMessage("Carte volée et placée!");
    }

    public void gameLoop() {
        distribuerCartesInitiales();
        view.afficherPlateau(plateau, variante.getNombreBornes());

        boolean jeuTactique = variante.hasCartesTactiques();
        if (jeuTactique) {
            view.afficherMessage("=== MODE TACTIQUE ACTIVÉ ===");
        }

        while (!gameEnded) {
            Joueur joueurActuel = getJoueurActuel();
            Joueur adversaire = getJoueurAdverse();

            int cartesTactiquesRestantes = (deckTactique != null) ? deckTactique.getDeckSize() : -1;
            view.afficherDeckInfo(deckClan.getDeckSize(), cartesTactiquesRestantes);
            view.afficherMainJoueur(joueurActuel);

            //TO DO
            if (jeuTactique) {
                view.afficherInfoTactique(joueur1.getCartesTactiquesJouees(), joueur2.getCartesTactiquesJouees());
            }

            // Pioche
            if (joueurActuel.isAI()) {
                if (jeuTactique) {
                    int choixPioche = IAAleatoire.choisirTypePioche(!deckClan.isEmpty(), deckTactique != null && !deckTactique.isEmpty());
                    piocher(joueurActuel, choixPioche);
                } else {
                    piocher(joueurActuel, 1);
                }
            } else {
                if (jeuTactique) {
                    int choixPioche = view.demanderChoixPioche(!deckClan.isEmpty(), deckTactique != null && !deckTactique.isEmpty());
                    piocher(joueurActuel, choixPioche);
                } else {
                    piocher(joueurActuel, 1);
                }
            }
            view.afficherMainJoueur(joueurActuel);

            // Jouer une carte
            boolean peutJouerTactique = gestionTactique != null && gestionTactique.peutJouerCarteTactique(joueurActuel, adversaire);
            boolean hasCartesClan = !joueurActuel.getCartesClan().isEmpty();
            if (joueurActuel.isAI()) {
                int typeAction = IAAleatoire.choisirTypeCarteAJouer(hasCartesClan, peutJouerTactique);

                if (typeAction == 1) jouerCarteIA(joueurActuel);
                else if (typeAction == 2) jouerCarteTactiqueIA(joueurActuel);
                else {
                    view.afficherMessage(joueurActuel.getName() + " passe son tour.");
                    incrementerTourPasse();
                }
            } else {
                boolean hasCarteTactique = joueurActuel.hasCarteTactique();

                if (jeuTactique && (peutJouerTactique || hasCarteTactique)) {
                    int typeAction = view.demanderTypeCarteAJouer(hasCartesClan, peutJouerTactique);
                    if (typeAction == 1) jouerCarteClan(joueurActuel);
                    else if (typeAction == 2) jouerCarteTactique(joueurActuel);
                } else {
                    jouerCarteClan(joueurActuel);
                }
            }

            if (jeuBloqueParToursPasses()) {
                view.afficherMessage("Trop de tours passés consécutifs. Fin de partie.");
                gameEnded = true;
            }

            // Évaluer les bornes complètes
            int indexBorne = trouverBorneAEvaluer();
            while (indexBorne != -1) {
                evaluerBorne(indexBorne);
                indexBorne = trouverBorneAEvaluer();
            }

            gameEnded = gameEnded || aGagne(1) || aGagne(2);

            view.afficherPlateau(plateau, variante.getNombreBornes());
            view.afficherSeparateur();

            if (!joueurActuel.isAI()) view.attendreTouche("  Appuyez sur Entrer pour continuer...");

            if (!gameEnded) {
                if (!joueurActuel.isAI()) view.afficherTransition(getJoueurAdverse().getName());
                changerJoueurCourant();
            }
        }

        if (jeuBloqueParToursPasses()) {
            // Compter les bornes contrôlées
            int bornesJ1 = getNombreBornesControlees(1);
            int bornesJ2 = getNombreBornesControlees(2);

            int winner;
            String nomGagnant;

            if (bornesJ1 > bornesJ2) {
                winner = 1;
                nomGagnant = joueur1.getName();
            } else if (bornesJ2 > bornesJ1) {
                winner = 2;
                nomGagnant = joueur2.getName();
            } else {
                winner = 0;
                nomGagnant = "Personne n'a gagnee";
                view.afficherMessage("Égalité!!!!! ");
            }

            view.afficherScoreFinal(bornesJ1, bornesJ2, joueur1.getName(), joueur2.getName());
            view.afficherFinPartie(winner, nomGagnant + Constants.CYAN + " GAME ENDED BY PASSING TO MANY ROUNDS !!!!!!");
        } else {
            // Victoire normale
            int winner = aGagne(1) ? 1 : 2;
            String nomGagnant = (winner == 1) ? joueur1.getName() : joueur2.getName();
            view.afficherScoreFinal(getNombreBornesControlees(1), getNombreBornesControlees(2), joueur1.getName(), joueur2.getName());
            view.afficherFinPartie(winner, nomGagnant);
        }

        rejouer();
    }

    private void rejouer() {

        if (view.demanderRejouer() == 1) {
            mainGame();
        } else {
            view.afficherMessage("Merci d'avoir jouer");
        }
    }

    public void mainGame() {
        view.afficherTitre();

        int choixVariante = view.afficherMenuVariante();
        VarianteJeu variante = switch (choixVariante) {
            case 2 -> VarianteJeu.TACTIQUE;
            case 3 -> VarianteJeu.EXPRESS;
            case 4 -> VarianteJeu.EXPRESS_TACTIQUE;
            default -> VarianteJeu.BASE;
        };


        view.afficherVarianteChoisie(variante.getNom());

        view.afficherConfigJoueur(1);
        String nomJ1 = view.demanderNomJoueur("Joueur 1");
        boolean isAI1 = (view.demanderTypeJoueur() == 2);
        Joueur joueur1 = new Joueur(nomJ1, isAI1);

        view.afficherConfigJoueur(2);
        String nomJ2 = view.demanderNomJoueur("Joueur 2");
        boolean isAI2 = (view.demanderTypeJoueur() == 2);
        Joueur joueur2 = new Joueur(nomJ2, isAI2);

        view.afficherLancementPartie(nomJ1, nomJ2, variante.getNom());

        Jeu jeu = JeuFactory.creerJeu(variante, joueur1, joueur2);
        jeu.gameLoop();
    }
}