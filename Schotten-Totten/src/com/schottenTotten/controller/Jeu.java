package com.schottenTotten.controller;

import java.util.*;
import com.schottenTotten.ai.IAAleatoire;
import com.schottenTotten.model.*;
import com.schottenTotten.model.carte.*;
import com.schottenTotten.model.decks.*;
import com.schottenTotten.model.enums.*;
import com.schottenTotten.utils.Constants;
import com.schottenTotten.view.ConsoleView;

public class Jeu implements IControleurJeu {

    // Composants du jeu
    private final Plateau plateau;
    private final DeckClan deckClan;
    private final DeckTactique deckTactique;
    private final Joueur joueur1;
    private final Joueur joueur2;
    private final VarianteJeu variante;
    private final ConsoleView view;

    // Gestionnaire
    private final GestionTactique gestionTactique;

    // État du jeu
    private Tour tourActuel;
    private boolean gameEnded;
    private int toursPassesConsecutifs;
    private int gagnant;

    // Constantes
    private static final int MAX_TOURS_PASSES = 4;

    // ENUM TOUR
    public enum Tour {
        JOUEUR1(1),
        JOUEUR2(2);

        private final int numero;

        Tour(int numero) {
            this.numero = numero;
        }

        public int getNumero() {
            return numero;
        }

        public Tour suivant() {
            return this == JOUEUR1 ? JOUEUR2 : JOUEUR1;
        }
    }

    // CONSTRUCTEURS

    public Jeu(VarianteJeu variante, Joueur joueur1, Joueur joueur2) {
        this.variante = variante;
        this.plateau = new Plateau(variante.getNombreBornes());
        this.deckClan = new DeckClan();
        this.deckClan.shuffle();
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.tourActuel = Tour.JOUEUR1;
        this.gameEnded = false;
        this.view = new ConsoleView();
        this.toursPassesConsecutifs = 0;
        this.gagnant = 0;

        // Initialisation conditionnelle du mode tactique
        if (variante.hasCartesTactiques()) {
            this.deckTactique = new DeckTactique();
            this.deckTactique.shuffle();
            this.gestionTactique = new GestionTactique();
        } else {
            this.deckTactique = null;
            this.gestionTactique = null;
        }
    }

    public Jeu() {
        this(VarianteJeu.BASE,
                new Joueur("nomJ1", true),
                new Joueur("nomJ2", true));
    }

    // IMPLÉMENTATION INTERFACE - Getters de base

    @Override
    public Plateau getPlateau() {
        return plateau;
    }

    @Override
    public VarianteJeu getVariante() {
        return variante;
    }

    @Override
    public int getJoueurCourant() {
        return tourActuel.getNumero();
    }

    @Override
    public Joueur getJoueur(int numero) {
        return numero == 1 ? joueur1 : joueur2;
    }

    @Override
    public DeckClan getDeckClan() {
        return deckClan;
    }

    @Override
    public DeckTactique getDeckTactique() {
        return deckTactique;
    }

    // IMPLÉMENTATION INTERFACE - Opérations sur cartes

    @Override
    public boolean peutAjouterCarte(Borne borne, int joueur) {
        if (borne.isLocked()) return false;
        int max = borne.hasCombatDeBoue() ? 4 : 3;
        return borne.getNbCartes(joueur) < max;
    }

    @Override
    public void ajouterCarte(Borne borne, int joueur, Carte carte) {
        borne.addCarteToJoueurBorne(joueur, carte);
    }

    @Override
    public Carte retirerCarte(Borne borne, int joueur, int index) {
        return borne.removeCarte(joueur, index);
    }

    // IMPLÉMENTATION INTERFACE - Gestion tactique

    @Override
    public boolean peutJouerCarteTactique(Joueur joueur, Joueur adversaire) {
        if (gestionTactique == null) return false;
        return gestionTactique.peutJouerCarteTactique(joueur, adversaire);
    }

    @Override
    public boolean executerCarteTactique(CarteTactique carte, Joueur joueur, int joueurNum) {
        if (gestionTactique == null) return false;
        return gestionTactique.executerCarteTactique(carte, joueur, joueurNum);
    }

    @Override
    public void configurerTroupeElite(CarteTactique carte, Couleur couleur, int valeur) {
        if (gestionTactique != null) {
            gestionTactique.configurerTroupeElite(carte, couleur, valeur);
        }
    }

    // IMPLÉMENTATION INTERFACE - Contrôle du jeu

    @Override
    public void lancerPartie() {
        gameLoop();
    }

    @Override
    public boolean estTermine() {
        return gameEnded;
    }

    @Override
    public int getGagnant() {
        return gagnant;
    }

    // MÉTHODES PRIVÉES - Helpers

    private Joueur getJoueurActuel() {
        return getJoueur(tourActuel.getNumero());
    }

    private Joueur getJoueurAdverse() {
        return getJoueur(tourActuel.suivant().getNumero());
    }

    private void changerJoueurCourant() {
        tourActuel = tourActuel.suivant();
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

    private boolean isBorneFull(Borne borne) {
        int required = borne.hasCombatDeBoue() ? 4 : 3;
        return borne.getNbCartes(1) >= required && borne.getNbCartes(2) >= required;
    }

    // LOGIQUE DE JEU - Combinaisons et comparaisons

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

        boolean isCouleur = couleurs.stream().allMatch(c -> c != null && c.equals(couleurs.get(0)));
        boolean isBrelan = valeurs.stream().distinct().count() == 1;

        if (isSuite && isCouleur) return 4; // Suite couleur
        if (isBrelan) return 3;              // Brelan
        if (isCouleur) return 2;             // Couleur
        if (isSuite) return 1;               // Suite
        return 0;                            // Rien
    }

    private int getSommeCartes(List<Carte> cartes) {
        return cartes.stream().mapToInt(Carte::getValeur).sum();
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

        // Comparaison normale
        int typeJ1 = getTypeCombinaison(cartesJ1);
        int typeJ2 = getTypeCombinaison(cartesJ2);
        view.afficherCartesComparees(cartesJ1, cartesJ2, typeJ1, typeJ2);

        if (typeJ1 > typeJ2) return 1;
        if (typeJ2 > typeJ1) return 2;

        // Égalité de type : comparer les sommes
        int sommeJ1 = getSommeCartes(cartesJ1);
        int sommeJ2 = getSommeCartes(cartesJ2);
        if (sommeJ1 > sommeJ2) return 1;
        if (sommeJ2 > sommeJ1) return 2;

        // Égalité totale : dernier joueur
        return borne.getLastPlayer();
    }

    // LOGIQUE DE JEU - Conditions de victoire

    private int getNombreBornesControlees(int joueur) {
        int count = 0;
        for (int i = 0; i < variante.getNombreBornes(); i++) {
            if (plateau.getBorne(i).getProprietaire() == joueur) {
                count++;
            }
        }
        return count;
    }

    private boolean aGagne(int joueur) {
        // Victoire par nombre de bornes
        if (getNombreBornesControlees(joueur) >= variante.getBornesToWin()) {
            return true;
        }

        // Victoire par suite de bornes
        int suite = 0;
        for (int i = 0; i < variante.getNombreBornes(); i++) {
            if (plateau.getBorne(i).getProprietaire() == joueur) {
                suite++;
                if (suite >= variante.getSuiteBornes()) {
                    return true;
                }
            } else {
                suite = 0;
            }
        }
        return false;
    }

    // LOGIQUE DE JEU - Distribution et pioche

    private void distribuerCartesInitiales() {
        for (int i = 0; i < variante.getCartesInitiales(); i++) {
            joueur1.addCartesToHand(deckClan.piocher(1));
            joueur2.addCartesToHand(deckClan.piocher(1));
        }
    }

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

    // LOGIQUE DE JEU - Évaluation des bornes

    private int trouverBorneAEvaluer() {
        for (int i = 0; i < variante.getNombreBornes(); i++) {
            Borne borne = plateau.getBorne(i);
            if (isBorneFull(borne) && !borne.isLocked()) {
                return i;
            }
        }
        return -1;
    }

    private void evaluerBorne(int indexBorne) {
        Borne borne = plateau.getBorne(indexBorne);
        int gagnantBorne = comparerBorne(borne);
        borne.setProprietaire(gagnantBorne);
        borne.setLocked(true);
        view.afficherGagnantBorne(gagnantBorne, indexBorne);
    }

    // LOGIQUE DE JEU - Actions des joueurs

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

        while (!peutAjouterCarte(borne, getJoueurCourant())) {
            view.afficherMessage("Borne invalide. Réessayez.");
            indexBorne = view.choseBorneToPlay(joueur, variante.getNombreBornes());
            borne = plateau.getBorne(indexBorne);
        }

        ajouterCarte(borne, getJoueurCourant(), carte);
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

        boolean succes = executerCarteTactique(carte, joueur, getJoueurCourant());
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

        ajouterCarte(borne, getJoueurCourant(), carteClan);
        joueur.removeCarteFromHand(carteClan);
        view.afficherCarteJouee(joueur.getName(), carteClan, indexBorne);
        reinitialiserToursPasses();
    }

    private void jouerCarteTactiqueIA(Joueur joueur) {
        if (gestionTactique == null) return;
        gestionTactique.jouerCarteTactiqueIA(joueur);
    }

    // BOUCLE PRINCIPALE DU JEU

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

            // Affichage de l'état
            int cartesTactiquesRestantes = (deckTactique != null) ? deckTactique.getDeckSize() : -1;
            view.afficherDeckInfo(deckClan.getDeckSize(), cartesTactiquesRestantes);
            view.afficherMainJoueur(joueurActuel);

            if (jeuTactique) {
                view.afficherInfoTactique(
                        joueur1.getCartesTactiquesJouees(),
                        joueur2.getCartesTactiquesJouees()
                );
            }

            // Phase de pioche
            gererPhasePioche(joueurActuel, jeuTactique);
            view.afficherMainJoueur(joueurActuel);

            // Phase de jeu
            gererPhaseJeu(joueurActuel, adversaire, jeuTactique);

            // Vérification du blocage
            if (jeuBloqueParToursPasses()) {
                view.afficherMessage("Trop de tours passés consécutifs. Fin de partie.");
                gameEnded = true;
            }

            // Évaluation des bornes
            evaluerBornesCompletes();

            // Vérification de victoire
            gameEnded = gameEnded || aGagne(1) || aGagne(2);

            // Affichage et transition
            view.afficherPlateau(plateau, variante.getNombreBornes());
            view.afficherSeparateur();

            if (!joueurActuel.isAI()) {
                view.attendreTouche("  Appuyez sur Entrer pour continuer...");
            }

            if (!gameEnded) {
                if (!joueurActuel.isAI()) {
                    view.afficherTransition(getJoueurAdverse().getName());
                }
                changerJoueurCourant();
            }
        }

        afficherResultatFinal();
        rejouer();
    }

    private void gererPhasePioche(Joueur joueurActuel, boolean jeuTactique) {
        if (joueurActuel.isAI()) {
            if (jeuTactique) {
                int choixPioche = IAAleatoire.choisirTypePioche(
                        !deckClan.isEmpty(),
                        deckTactique != null && !deckTactique.isEmpty()
                );
                piocher(joueurActuel, choixPioche);
            } else {
                piocher(joueurActuel, 1);
            }
        } else {
            if (jeuTactique) {
                int choixPioche = view.demanderChoixPioche(
                        !deckClan.isEmpty(),
                        deckTactique != null && !deckTactique.isEmpty()
                );
                piocher(joueurActuel, choixPioche);
            } else {
                piocher(joueurActuel, 1);
            }
        }
    }

    private void gererPhaseJeu(Joueur joueurActuel, Joueur adversaire, boolean jeuTactique) {
        boolean peutJouerTactique = peutJouerCarteTactique(joueurActuel, adversaire);
        boolean hasCartesClan = !joueurActuel.getCartesClan().isEmpty();

        if (joueurActuel.isAI()) {
            int typeAction = IAAleatoire.choisirTypeCarteAJouer(hasCartesClan, peutJouerTactique);

            if (typeAction == 1) {
                jouerCarteIA(joueurActuel);
            } else if (typeAction == 2) {
                jouerCarteTactiqueIA(joueurActuel);
            } else {
                view.afficherMessage(joueurActuel.getName() + " passe son tour.");
                incrementerTourPasse();
            }
        } else {
            boolean hasCarteTactique = joueurActuel.hasCarteTactique();

            if (jeuTactique && (peutJouerTactique || hasCarteTactique)) {
                int typeAction = view.demanderTypeCarteAJouer(hasCartesClan, peutJouerTactique);
                if (typeAction == 1) {
                    jouerCarteClan(joueurActuel);
                } else if (typeAction == 2) {
                    jouerCarteTactique(joueurActuel);
                }
            } else {
                jouerCarteClan(joueurActuel);
            }
        }
    }

    private void evaluerBornesCompletes() {
        int indexBorne = trouverBorneAEvaluer();
        while (indexBorne != -1) {
            evaluerBorne(indexBorne);
            indexBorne = trouverBorneAEvaluer();
        }
    }

    private void afficherResultatFinal() {
        if (jeuBloqueParToursPasses()) {
            int bornesJ1 = getNombreBornesControlees(1);
            int bornesJ2 = getNombreBornesControlees(2);

            if (bornesJ1 > bornesJ2) {
                gagnant = 1;
            } else if (bornesJ2 > bornesJ1) {
                gagnant = 2;
            } else {
                gagnant = 0;
                view.afficherMessage("Égalité!!!!! ");
            }

            String nomGagnant = gagnant == 1 ? joueur1.getName() :
                    gagnant == 2 ? joueur2.getName() : "Personne n'a gagnee";
            view.afficherScoreFinal(bornesJ1, bornesJ2, joueur1.getName(), joueur2.getName());
            view.afficherFinPartie(gagnant, nomGagnant + Constants.CYAN +
                    " GAME ENDED BY PASSING TOO MANY ROUNDS !!!!!!");
        } else {
            gagnant = aGagne(1) ? 1 : 2;
            String nomGagnant = (gagnant == 1) ? joueur1.getName() : joueur2.getName();
            view.afficherScoreFinal(
                    getNombreBornesControlees(1),
                    getNombreBornesControlees(2),
                    joueur1.getName(),
                    joueur2.getName()
            );
            view.afficherFinPartie(gagnant, nomGagnant);
        }
    }

    private void rejouer() {
        if (view.demanderRejouer() == 1) {
            mainGame();
        } else {
            view.afficherMessage("Merci d'avoir joué");
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

    // INNER CLASS - GESTION TACTIQUE

    /**
     * Classe interne privée pour gérer toutes les opérations tactiques.
     * A accès direct à tous les membres de Jeu (classe englobante).
     */
    private class GestionTactique {

        public boolean peutJouerCarteTactique(Joueur joueur, Joueur adversaire) {
            return joueur.getCartesTactiquesJouees() <= adversaire.getCartesTactiquesJouees()
                    && joueur.hasCarteTactique();
        }

        public void configurerTroupeElite(CarteTactique carte, Couleur couleur, int valeur) {
            TypeCarteTactique type = carte.getType();
            switch (type) {
                case JOKER:
                    validerValeur(valeur, 1, 9, "Joker");
                    break;
                case ESPION:
                    valeur = 7;
                    break;
                case PORTE_BOUCLIER:
                    validerValeur(valeur, 1, 3, "Porte-Bouclier");
                    break;
                default:
                    throw new IllegalArgumentException("Ce n'est pas une troupe d'élite");
            }
            carte.setCouleur(couleur);
            carte.setValeur(valeur);
        }

        public boolean executerCarteTactique(CarteTactique carte, Joueur joueur, int joueurNum) {
            TypeCarteTactique type = carte.getType();

            switch (type.getCategorie()) {
                case TROUPE_ELITE:
                    return executerTroupeElite(carte, joueur, joueurNum);
                case MODE_COMBAT:
                    return executerModeCombat(carte, joueur);
                case RUSE:
                    return executerRuse(carte, joueur, joueurNum);
                default:
                    return false;
            }
        }

        // === Méthodes helper ===

        private void validerValeur(int valeur, int min, int max, String nomCarte) {
            if (valeur < min || valeur > max) {
                throw new IllegalArgumentException(
                        String.format("Valeur invalide pour %s (%d-%d)", nomCarte, min, max)
                );
            }
        }

        private Borne demanderBorneValide(Joueur joueur, boolean checkLocked) {
            int index = view.demanderBorne(joueur, variante.getNombreBornes());
            Borne borne = plateau.getBorne(index);

            while (!isBorneValide(borne, checkLocked)) {
                view.afficherMessage(
                        checkLocked ? "Cette borne est déjà revendiquée." : "Borne invalide."
                );
                index = view.demanderBorne(joueur, variante.getNombreBornes());
                borne = plateau.getBorne(index);
            }
            return borne;
        }

        private boolean isBorneValide(Borne borne, boolean checkLocked) {
            return checkLocked
                    ? !borne.isLocked()
                    : peutAjouterCarte(borne, getJoueurCourant());
        }

        private int getBorneIndex(Borne borne) {
            for (int i = 0; i < variante.getNombreBornes(); i++) {
                if (plateau.getBorne(i) == borne) {
                    return i;
                }
            }
            return -1;
        }

        // === Exécution des troupes d'élite ===

        private boolean executerTroupeElite(CarteTactique carte, Joueur joueur, int joueurNum) {
            Couleur couleur = view.demanderCouleur();
            int valeur = determinerValeurTroupeElite(carte);

            if (valeur == -1) return false;

            configurerTroupeElite(carte, couleur, valeur);
            Borne borne = demanderBorneValide(joueur, false);
            int indexBorne = getBorneIndex(borne);

            ajouterCarte(borne, joueurNum, carte);
            view.afficherMessage("Troupe d'élite jouée sur la borne " + indexBorne);
            return true;
        }

        private int determinerValeurTroupeElite(CarteTactique carte) {
            switch (carte.getType()) {
                case JOKER:
                    return view.demanderValeur(1, 9);
                case ESPION:
                    view.afficherMessage("L'Espion a une valeur fixe de 7.");
                    return 7;
                case PORTE_BOUCLIER:
                    return view.demanderValeur(1, 3);
                default:
                    return -1;
            }
        }

        // === Exécution des modes de combat ===

        private boolean executerModeCombat(CarteTactique carte, Joueur joueur) {
            view.afficherMessage("Choisissez une borne pour " + carte.getType().getNom());
            Borne borne = demanderBorneValide(joueur, true);
            int indexBorne = getBorneIndex(borne);

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

            view.afficherModeCombatApplique(carte.getType().getNom(), indexBorne);
            return true;
        }

        // === Exécution des ruses ===

        private boolean executerRuse(CarteTactique carte, Joueur joueur, int joueurNum) {
            int adversaireNum = (joueurNum == 1) ? 2 : 1;

            switch (carte.getType()) {
                case CHASSEUR_DE_TETE:
                    return executerChasseurDeTete(joueur);
                case STRATEGIE:
                    return executerStratege(joueurNum);
                case BANSHEE:
                    return executerBanshee(adversaireNum);
                case TRAITRE:
                    return executerTraitre(joueurNum, adversaireNum);
                default:
                    return false;
            }
        }

        private boolean executerChasseurDeTete(Joueur joueur) {
            view.afficherMessage("=== Chasseur de Tête ===");
            view.afficherMessage("Piochez 3 cartes, gardez-en 2.");

            List<Carte> piochees = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                int choix = view.demanderChoixPioche(
                        !deckClan.isEmpty(),
                        deckTactique != null && !deckTactique.isEmpty()
                );

                if (choix == 1 && !deckClan.isEmpty()) {
                    piochees.addAll(deckClan.piocher(1));
                } else if (choix == 2 && deckTactique != null && !deckTactique.isEmpty()) {
                    piochees.addAll(deckTactique.piocher(1));
                }
            }

            if (piochees.isEmpty()) {
                view.afficherMessage("Aucune carte piochée!");
                return false;
            }

            view.afficherMessage("Cartes piochées:");
            for (int i = 0; i < piochees.size(); i++) {
                view.afficherMessage(i + ": " + piochees.get(i));
            }

            int indexDefausse = view.demanderIndex("Carte à défausser", piochees.size());
            piochees.remove(indexDefausse);

            for (Carte c : piochees) {
                joueur.addCartesToHand(Collections.singletonList(c));
            }
            view.afficherMessage("Cartes ajoutées à votre main.");
            return true;
        }

        private boolean executerStratege(int joueurNum) {
            view.afficherMessage("=== Stratège ===");
            int nombreBornes = variante.getNombreBornes();

            int borneSource = view.demanderIndex("Borne source", nombreBornes);
            Borne source = plateau.getBorne(borneSource);
            List<Carte> carteClans = source.getCartesParJoueur(joueurNum);

            if (carteClans.isEmpty()) {
                view.afficherMessage("Aucune carte sur cette borne!");
                return false;
            }

            view.afficherMessage("Cartes:");
            for (int i = 0; i < carteClans.size(); i++) {
                view.afficherMessage(i + ": " + carteClans.get(i));
            }

            int indexCarte = view.demanderIndex("Carte à déplacer/défausser", carteClans.size());
            int action = view.demanderChoixStratege();

            if (action == 1) {
                int borneDest = view.demanderIndex("Borne destination", nombreBornes);
                Borne dest = plateau.getBorne(borneDest);

                if (!peutAjouterCarte(dest, joueurNum)) {
                    view.afficherMessage("Destination invalide!");
                    return false;
                }

                Carte carteClan = retirerCarte(source, joueurNum, indexCarte);
                ajouterCarte(dest, joueurNum, carteClan);
                view.afficherMessage("Carte déplacée.");
            } else {
                retirerCarte(source, joueurNum, indexCarte);
                view.afficherMessage("Carte défaussée.");
            }
            return true;
        }

        private List<Integer> getIndicesCartesClan(List<Carte> cartes) {
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < cartes.size(); i++) {
                if (!cartes.get(i).isTactique()) {
                    indices.add(i);
                }
            }
            return indices;
        }

        private boolean executerBanshee(int adversaireNum) {
            view.afficherMessage("=== Banshee ===");
            int nombreBornes = variante.getNombreBornes();

            int indexBorne = view.demanderIndex("Borne cible", nombreBornes);
            Borne borne = plateau.getBorne(indexBorne);

            if (borne.isLocked()) {
                view.afficherMessage("Borne déjà revendiquée!");
                return false;
            }

            List<Carte> cartesAdversaire = borne.getCartesParJoueur(adversaireNum);
            if (cartesAdversaire.isEmpty()) {
                view.afficherMessage("Aucune carte adverse!");
                return false;
            }

            List<Integer> indicesValides = getIndicesCartesClan(cartesAdversaire);
            if (indicesValides.isEmpty()) {
                view.afficherMessage("Aucune carte clan à défausser!");
                return false;
            }

            view.afficherMessage("Cartes adverses (clan uniquement):");
            for (int i = 0; i < indicesValides.size(); i++) {
                view.afficherMessage(i + ": " + cartesAdversaire.get(indicesValides.get(i)));
            }

            int choix = view.demanderIndex("Carte à défausser", indicesValides.size());
            retirerCarte(borne, adversaireNum, indicesValides.get(choix));
            view.afficherMessage("Carte défaussée!");
            return true;
        }

        private boolean executerTraitre(int joueurNum, int adversaireNum) {
            view.afficherMessage("=== Traître ===");
            int nombreBornes = variante.getNombreBornes();

            int borneSource = view.demanderIndex("Borne source (adverse)", nombreBornes);
            Borne source = plateau.getBorne(borneSource);

            if (source.isLocked()) {
                view.afficherMessage("Borne déjà revendiquée!");
                return false;
            }

            List<Carte> cartesAdversaire = source.getCartesParJoueur(adversaireNum);
            if (cartesAdversaire.isEmpty()) {
                view.afficherMessage("Aucune carte adverse!");
                return false;
            }

            List<Integer> indicesValides = getIndicesCartesClan(cartesAdversaire);
            if (indicesValides.isEmpty()) {
                view.afficherMessage("Aucune carte clan à voler!");
                return false;
            }

            view.afficherMessage("Cartes adverses (clan uniquement):");
            for (int i = 0; i < indicesValides.size(); i++) {
                view.afficherMessage(i + ": " + cartesAdversaire.get(indicesValides.get(i)));
            }

            int choix = view.demanderIndex("Carte à voler", indicesValides.size());
            Carte carteClan = retirerCarte(source, adversaireNum, indicesValides.get(choix));

            int borneDest = view.demanderIndex("Borne destination", nombreBornes);
            Borne dest = plateau.getBorne(borneDest);

            if (!peutAjouterCarte(dest, joueurNum)) {
                view.afficherMessage("Destination invalide!");
                return false;
            }

            ajouterCarte(dest, joueurNum, carteClan);
            view.afficherMessage("Carte volée et placée!");
            return true;
        }

        // === Logique IA pour les cartes tactiques ===

        public void jouerCarteTactiqueIA(Joueur joueur) {
            List<CarteTactique> cartesTactiques = joueur.getCartesTactiques();
            if (cartesTactiques.isEmpty()) return;

            CarteTactique carte = IAAleatoire.choisirCarteTactique(cartesTactiques);
            if (carte == null) return;

            TypeCarteTactique type = carte.getType();

            boolean carteJouee = false;

            if (type.getCategorie() == TypeCarteTactique.TypeCategorie.TROUPE_ELITE) {
                carteJouee = jouerTroupeEliteIA(joueur, carte, type);
            } else if (type.getCategorie() == TypeCarteTactique.TypeCategorie.MODE_COMBAT) {
                carteJouee = jouerModeCombatIA(joueur, carte, type);
            } else if (type.getCategorie() == TypeCarteTactique.TypeCategorie.RUSE) {
                carteJouee = jouerRuseIA(carte, type);
            }

            if (carteJouee) {
                joueur.removeCarteFromHand(carte);
                joueur.incrementerCartesTactiquesJouees();
                reinitialiserToursPasses();
            } else {
                // Si la carte tactique n'a pas pu être jouée, jouer une carte clan
                List<CarteClan> cartesClan = joueur.getCartesClan();
                if (!cartesClan.isEmpty()) {
                    jouerCarteIA(joueur);
                } else {
                    view.afficherMessage(joueur.getName() + " passe son tour.");
                    incrementerTourPasse();
                }
            }
        }

        private boolean jouerTroupeEliteIA(Joueur joueur, CarteTactique carte, TypeCarteTactique type) {
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
                    return false;
            }

            carte.setCouleur(couleur);
            carte.setValeur(valeur);

            int indexBorne = IAAleatoire.choisirBorne(Jeu.this);
            Borne borne = plateau.getBorne(indexBorne);
            ajouterCarte(borne, getJoueurCourant(), carte);
            view.afficherMessage(joueur.getName() + " joue " + carte + " sur la borne " + indexBorne);
            return true;
        }

        private boolean jouerModeCombatIA(Joueur joueur, CarteTactique carte, TypeCarteTactique type) {
            int indexBorne = IAAleatoire.choisirBorneNonRevendiquee(Jeu.this);
            Borne borne = plateau.getBorne(indexBorne);

            if (type == TypeCarteTactique.COLIN_MAILLARD) {
                borne.setColinMaillard(true);
            } else if (type == TypeCarteTactique.COMBAT_DE_BOUE) {
                borne.setCombatDeBoue(true);
            }

            view.afficherMessage(joueur.getName() + " joue " + type.getNom() + " sur la borne " + indexBorne);
            return true;
        }

        private boolean jouerRuseIA(CarteTactique carte, TypeCarteTactique type) {
            switch (type) {
                case CHASSEUR_DE_TETE:
                    return executerChasseurDeTeteIA();
                case STRATEGIE:
                    return executerStrategeIA(getJoueurCourant());
                case BANSHEE:
                    return executerBansheeIA((getJoueurCourant() % 2) + 1);
                case TRAITRE:
                    return executerTraitreIA(getJoueurCourant(), (getJoueurCourant() % 2) + 1);
                default:
                    return false;
            }
        }

        private boolean executerChasseurDeTeteIA() {
            view.afficherMessage("=== Chasseur de Tête (IA) ===");
            Joueur joueur = getJoueurActuel();
            List<Carte> piochees = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                int choix = IAAleatoire.choisirTypePioche(
                        !deckClan.isEmpty(),
                        deckTactique != null && !deckTactique.isEmpty()
                );
                if (choix == 1 && !deckClan.isEmpty()) {
                    piochees.addAll(deckClan.piocher(1));
                } else if (choix == 2 && deckTactique != null && !deckTactique.isEmpty()) {
                    piochees.addAll(deckTactique.piocher(1));
                }
            }

            if (piochees.isEmpty()) return false;

            int indexDefausse = IAAleatoire.choisirIndex(piochees.size());
            piochees.remove(indexDefausse);

            for (Carte c : piochees) {
                joueur.addCartesToHand(Collections.singletonList(c));
            }
            return true;
        }

        private boolean executerStrategeIA(int joueurNum) {
            int borneSource = IAAleatoire.choisirBorne(Jeu.this);
            Borne source = plateau.getBorne(borneSource);
            List<Carte> carteClans = source.getCartesParJoueur(joueurNum);

            if (carteClans.isEmpty()) return false;

            int indexCarte = IAAleatoire.choisirIndex(carteClans.size());
            int action = IAAleatoire.choisirValeur(1, 2);

            if (action == 1) {
                int borneDest = IAAleatoire.choisirIndex(variante.getNombreBornes());
                Borne dest = plateau.getBorne(borneDest);

                if (!peutAjouterCarte(dest, joueurNum)) return false;

                Carte carteClan = retirerCarte(source, joueurNum, indexCarte);
                ajouterCarte(dest, joueurNum, carteClan);
            } else {
                retirerCarte(source, joueurNum, indexCarte);
            }
            return true;
        }

        private boolean executerBansheeIA(int adversaireNum) {
            int indexBorne = IAAleatoire.choisirIndex(variante.getNombreBornes());
            Borne borne = plateau.getBorne(indexBorne);

            if (borne.isLocked()) return false;

            List<Carte> cartesAdversaire = borne.getCartesParJoueur(adversaireNum);
            if (cartesAdversaire.isEmpty()) return false;

            List<Integer> indicesValides = getIndicesCartesClan(cartesAdversaire);
            if (indicesValides.isEmpty()) return false;

            int choix = IAAleatoire.choisirIndex(indicesValides.size());
            retirerCarte(borne, adversaireNum, indicesValides.get(choix));
            return true;
        }

        private boolean executerTraitreIA(int joueurNum, int adversaireNum) {
            int borneSource = IAAleatoire.choisirIndex(variante.getNombreBornes());
            Borne source = plateau.getBorne(borneSource);

            if (source.isLocked()) return false;

            List<Carte> cartesAdversaire = source.getCartesParJoueur(adversaireNum);
            if (cartesAdversaire.isEmpty()) return false;

            List<Integer> indicesValides = getIndicesCartesClan(cartesAdversaire);
            if (indicesValides.isEmpty()) return false;

            int choix = IAAleatoire.choisirIndex(indicesValides.size());
            Carte carteClan = retirerCarte(source, adversaireNum, indicesValides.get(choix));

            int borneDest = IAAleatoire.choisirIndex(variante.getNombreBornes());
            Borne dest = plateau.getBorne(borneDest);

            if (!peutAjouterCarte(dest, joueurNum)) return false;

            ajouterCarte(dest, joueurNum, carteClan);
            return true;
        }
    }
}
