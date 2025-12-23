package com.schottenTotten.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.schottenTotten.ai.IAAleatoire;
import com.schottenTotten.model.*;
import com.schottenTotten.view.ConsoleView;

public class Jeu {
    private Plateau plateau;
    private Deck deck;
    private DeckTactique deckTactique;
    private Joueur joueur1;
    private Joueur joueur2;
    private VarianteJeu variante;
    private int joueurCourant;
    private boolean gameEnded;
    private ConsoleView view;
    private GestionTactique gestionTactique;

    public Jeu(VarianteJeu variante, String nomJ1, boolean isAI1, String nomJ2, boolean isAI2) {
        this.variante=variante;
        this.plateau=new Plateau(variante.getNombreBornes());
        this.deck=new Deck();
        this.deck.shuffle();
        this.joueur1=new Joueur(nomJ1,isAI1);
        this.joueur2=new Joueur(nomJ2,isAI2);
        this.joueurCourant=1;
        this.gameEnded=false;
        this.view=new ConsoleView();

        if (variante.hasCartesTactiques()) {
            this.deckTactique=new DeckTactique();
            this.deckTactique.shuffle();
            this.gestionTactique=new GestionTactique(this,view);
        }
    }

    public Plateau getPlateau() { return plateau; }
    public Deck getDeck() { return deck; }
    public DeckTactique getDeckTactique() { return deckTactique; }
    public Joueur getJoueur1() { return joueur1; }
    public Joueur getJoueur2() { return joueur2; }
    public int getJoueurCourant() { return joueurCourant; }
    public VarianteJeu getVariante() { return variante; }

    // Getter unifié pour récupérer un joueur par numéro
    public Joueur getJoueur(int num) { return num==1 ? joueur1 : joueur2; }
    private Joueur getJoueurActuel() { return getJoueur(joueurCourant); }
    private Joueur getJoueurAdverse() { return getJoueur(joueurCourant==1 ? 2 : 1); }

    public boolean peutAjouterCarte(Borne borne, int joueur) {
        if (borne.isLocked()) return false;
        int max=borne.hasCombatDeBoue() ? 4 : 3;
        return borne.getNbCartes(joueur)<max;
    }

    public void ajouterCarte(Borne borne, int joueur, Carte carte) {
        borne.addCarte(joueur,carte);
    }

    public Carte retirerCarte(Borne borne, int joueur, int index) {
        return borne.removeCarte(joueur,index);
    }

    private boolean isBorneFull(Borne borne) {
        int required=borne.hasCombatDeBoue() ? 4 : 3;
        return borne.getNbCartes(1)>=required && borne.getNbCartes(2)>=required;
    }

    private int getTypeCombinaison(List<Carte> cartes) {
        if (cartes.size()<3) return 0;

        List<Integer> valeurs=new ArrayList<>();
        List<Couleur> couleurs=new ArrayList<>();
        for (Carte carte : cartes) {
            valeurs.add(carte.getValeur());
            couleurs.add(carte.getCouleur());
        }
        Collections.sort(valeurs);

        boolean isSuite=true;
        for (int i=1; i<valeurs.size(); i++) {
            if (valeurs.get(i)-valeurs.get(i-1)!=1) { isSuite=false; break; }
        }
        boolean isCouleur=couleurs.stream().allMatch(c -> c!=null && c.equals(couleurs.get(0)));
        boolean isBrelan=valeurs.stream().distinct().count()==1;

        if (isSuite && isCouleur) return 4;
        if (isBrelan) return 3;
        if (isCouleur) return 2;
        if (isSuite) return 1;
        return 0;
    }

    private int getSommeCartes(List<Carte> cartes) {
        int somme=0;
        for (Carte carte : cartes) somme+=carte.getValeur();
        return somme;
    }

    private int comparerBorne(Borne borne) {
        List<Carte> cartesJ1=borne.getCartesJ1();
        List<Carte> cartesJ2=borne.getCartesJ2();

        // Colin-Maillard : seule la somme compte
        if (borne.hasColinMaillard()) {
            int sommeJ1=getSommeCartes(cartesJ1);
            int sommeJ2=getSommeCartes(cartesJ2);
            if (sommeJ1>sommeJ2) return 1;
            if (sommeJ2>sommeJ1) return 2;
            return borne.getLastPlayer();
        }

        int typeJ1=getTypeCombinaison(cartesJ1);
        int typeJ2=getTypeCombinaison(cartesJ2);
        view.afficherCartesComparees(cartesJ1,cartesJ2,typeJ1,typeJ2);

        if (typeJ1>typeJ2) return 1;
        if (typeJ2>typeJ1) return 2;

        int sommeJ1=getSommeCartes(cartesJ1);
        int sommeJ2=getSommeCartes(cartesJ2);
        if (sommeJ1>sommeJ2) return 1;
        if (sommeJ2>sommeJ1) return 2;
        return borne.getLastPlayer();
    }

    private int getNombreBornesControlees(int joueur) {
        int count=0;
        for (int i=0; i<variante.getNombreBornes(); i++) {
            if (plateau.getBorne(i).getProprietaire()==joueur) count++;
        }
        return count;
    }

    private boolean aGagne(int joueur) {
        if (getNombreBornesControlees(joueur)>=variante.getBornesToWin()) return true;
        int suite=0;
        for (int i=0; i<variante.getNombreBornes(); i++) {
            if (plateau.getBorne(i).getProprietaire()==joueur) {
                suite++;
                if (suite>=variante.getSuiteBornes()) return true;
            } else {
                suite=0;
            }
        }
        return false;
    }

    private void distribuerCartesInitiales() {
        for (int i=0; i<variante.getCartesInitiales(); i++) {
            joueur1.addCarteToHand(deck.piocher(1));
            joueur2.addCarteToHand(deck.piocher(1));
        }
    }

    // Pioche unifiée : type 1=clan, type 2=tactique
    private void piocher(Joueur joueur, int type) {
        List<Carte> cartes;
        if (type==1) {
            cartes=deck.piocher(1);
            if (cartes.isEmpty()) { view.afficherDeckVide(); return; }
        } else {
            if (deckTactique==null || deckTactique.isEmpty()) return;
            cartes=deckTactique.piocher(1);
        }
        joueur.addCarteToHand(cartes);
    }

    private void changerJoueurCourant() { joueurCourant=(joueurCourant==1) ? 2 : 1; }

    private int trouverBorneAEvaluer() {
        for (int i=0; i<variante.getNombreBornes(); i++) {
            Borne borne=plateau.getBorne(i);
            if (isBorneFull(borne) && !borne.isLocked()) return i;
        }
        return -1;
    }

    private void evaluerBorne(int indexBorne) {
        Borne borne=plateau.getBorne(indexBorne);
        int gagnant=comparerBorne(borne);
        borne.setProprietaire(gagnant);
        borne.setLocked(true);
        view.afficherGagnantBorne(gagnant,indexBorne);
    }

    private void jouerCarteClan(Joueur joueur) {
        List<Carte> cartesClan=joueur.getCartesClan();
        if (cartesClan.isEmpty()) {
            view.afficherMessage("Aucune carte clan disponible!");
            return;
        }

        Carte carte=view.AskCarteFromPlayer(joueur,joueur.getHandSize());
        while (carte instanceof CarteTactique) {
            view.afficherMessage("Choisissez une carte CLAN, pas tactique.");
            carte=view.AskCarteFromPlayer(joueur,joueur.getHandSize());
        }

        int indexBorne=view.choseBorneToPlay(joueur,variante.getNombreBornes());
        Borne borne=plateau.getBorne(indexBorne);
        while (!peutAjouterCarte(borne,joueurCourant)) {
            view.afficherMessage("Borne invalide. Réessayez.");
            indexBorne=view.choseBorneToPlay(joueur,variante.getNombreBornes());
            borne=plateau.getBorne(indexBorne);
        }

        ajouterCarte(borne,joueurCourant,carte);
        joueur.removeCarteFromHand(carte);
        view.afficherCarteJouee(joueur.getName(),carte,indexBorne);
    }

    private void jouerCarteTactique(Joueur joueur) {
        if (gestionTactique==null) return;

        List<CarteTactique> cartesTactiques=joueur.getCartesTactiques();
        view.afficherCartesTactiques(cartesTactiques);

        int indexCarte=view.demanderIndex("Choisissez une carte tactique",cartesTactiques.size());
        CarteTactique carte=cartesTactiques.get(indexCarte);

        boolean succes=gestionTactique.executerCarteTactique(carte,joueur,joueurCourant);
        if (succes) {
            joueur.removeCarteFromHand(carte);
            joueur.incrementerCartesTactiquesJouees();
        }
    }

    private void jouerCarteIA(Joueur joueur) {
        List<Carte> cartesClan=joueur.getCartesClan();
        if (cartesClan.isEmpty()) {
            view.afficherMessage(joueur.getName()+" passe son tour.");
            return;
        }

        Carte carte=IAAleatoire.choisirCarte(joueur,cartesClan);
        int indexBorne=IAAleatoire.choisirBorne(this);
        Borne borne=plateau.getBorne(indexBorne);

        ajouterCarte(borne,joueurCourant,carte);
        joueur.removeCarteFromHand(carte);
        view.afficherCarteJouee(joueur.getName(),carte,indexBorne);
    }

    private void jouerCarteTactiqueIA(Joueur joueur) {
        List<CarteTactique> cartesTactiques=joueur.getCartesTactiques();
        if (cartesTactiques.isEmpty()) return;

        CarteTactique carte=IAAleatoire.choisirCarteTactique(cartesTactiques);
        TypeCarteTactique type=carte.getType();

        // Troupes d'élite : configurer et placer sur une borne
        if (type.isTroupeElite()) {
            Couleur couleur=IAAleatoire.choisirCouleur();
            int valeur;
            switch (type) {
                case JOKER: valeur=IAAleatoire.choisirValeur(1,9); break;
                case ESPION: valeur=7; break;
                case PORTE_BOUCLIER: valeur=IAAleatoire.choisirValeur(1,3); break;
                default: return;
            }
            carte.setCouleurChoisie(couleur);
            carte.setValeurChoisie(valeur);

            int indexBorne=IAAleatoire.choisirBorne(this);
            Borne borne=plateau.getBorne(indexBorne);
            ajouterCarte(borne,joueurCourant,carte);
            joueur.removeCarteFromHand(carte);
            joueur.incrementerCartesTactiquesJouees();
            view.afficherMessage(joueur.getName()+" joue "+carte+" sur la borne "+indexBorne);
        }
        // Modes de combat : appliquer sur une borne non revendiquée
        else if (type.isModeCombat()) {
            int indexBorne=IAAleatoire.choisirBorneNonRevendiquee(this);
            Borne borne=plateau.getBorne(indexBorne);
            if (type==TypeCarteTactique.COLIN_MAILLARD) borne.setColinMaillard(true);
            else if (type==TypeCarteTactique.COMBAT_DE_BOUE) borne.setCombatDeBoue(true);
            joueur.removeCarteFromHand(carte);
            joueur.incrementerCartesTactiquesJouees();
            if (deckTactique!=null) deckTactique.defausser(carte);
            view.afficherMessage(joueur.getName()+" joue "+type.getNom()+" sur la borne "+indexBorne);
        }
        // Ruses : l'IA passe (trop complexe pour une IA basique)
        else if (type.isRuse()) {
            // L'IA basique ne joue pas les ruses, elle joue une carte clan à la place
            List<Carte> cartesClan=joueur.getCartesClan();
            if (!cartesClan.isEmpty()) {
                jouerCarteIA(joueur);
            } else {
                view.afficherMessage(joueur.getName()+" passe son tour.");
            }
        }
    }

    public void Gameloop() {
        distribuerCartesInitiales();
        view.afficherPlateau(plateau,variante.getNombreBornes());

        if (variante.hasCartesTactiques()) {
            view.afficherMessage("=== MODE TACTIQUE ACTIVÉ ===");
        }

        while (!gameEnded) {
            Joueur joueurActuel=getJoueurActuel();
            Joueur adversaire=getJoueurAdverse();

            int cartesTactiquesRestantes=(deckTactique!=null) ? deckTactique.getDeckSize() : -1;
            view.afficherDeckInfo(deck.getDeckSize(),cartesTactiquesRestantes);
            view.afficherMainJoueur(joueurActuel);

            if (variante.hasCartesTactiques()) {
                view.afficherInfoTactique(joueur1.getCartesTactiquesJouees(),joueur2.getCartesTactiquesJouees());
            }

            // Pioche
            if (joueurActuel.isAI()) {
                if (variante.hasCartesTactiques()) {
                    int choixPioche=IAAleatoire.choisirTypePioche(!deck.isEmpty(),deckTactique!=null && !deckTactique.isEmpty());
                    piocher(joueurActuel,choixPioche);
                } else {
                    piocher(joueurActuel,1);
                }
            } else {
                if (variante.hasCartesTactiques()) {
                    int choixPioche=view.demanderChoixPioche(!deck.isEmpty(),deckTactique!=null && !deckTactique.isEmpty());
                    piocher(joueurActuel,choixPioche);
                } else {
                    piocher(joueurActuel,1);
                }
            }
            view.afficherMainJoueur(joueurActuel);

            // Jouer une carte
            if (joueurActuel.isAI()) {
                boolean peutJouerTactique=gestionTactique!=null && gestionTactique.peutJouerCarteTactique(joueurActuel,adversaire);
                boolean hasCartesClan=!joueurActuel.getCartesClan().isEmpty();
                int typeAction=IAAleatoire.choisirTypeCarteAJouer(hasCartesClan,peutJouerTactique);

                if (typeAction==1) jouerCarteIA(joueurActuel);
                else if (typeAction==2) jouerCarteTactiqueIA(joueurActuel);
                else view.afficherMessage(joueurActuel.getName()+" passe son tour.");
            } else {
                boolean peutJouerTactique=gestionTactique!=null && gestionTactique.peutJouerCarteTactique(joueurActuel,adversaire);
                boolean hasCartesClan=!joueurActuel.getCartesClan().isEmpty();
                boolean hasCartesTact=joueurActuel.hasCarteTactique();

                if (variante.hasCartesTactiques() && (peutJouerTactique || hasCartesTact)) {
                    int typeAction=view.demanderTypeCarteAJouer(hasCartesClan,peutJouerTactique);
                    if (typeAction==1) jouerCarteClan(joueurActuel);
                    else if (typeAction==2) jouerCarteTactique(joueurActuel);
                } else {
                    jouerCarteClan(joueurActuel);
                }
            }

            // Évaluer les bornes complètes
            int indexBorne=trouverBorneAEvaluer();
            while (indexBorne!=-1) {
                evaluerBorne(indexBorne);
                indexBorne=trouverBorneAEvaluer();
            }

            gameEnded=aGagne(1) || aGagne(2);



            view.afficherPlateau(plateau,variante.getNombreBornes());
            view.afficherSeparateur();

            if (!joueurActuel.isAI()) view.attendreTouche("  Appuyez sur Entree pour continuee...");

            if (!gameEnded) {
                if (!joueurActuel.isAI()) view.afficherTransition(getJoueurAdverse().getName());
                changerJoueurCourant();
            }
        }

        int winner=aGagne(1) ? 1 : 2;
        String nomGagnant=(winner==1) ? joueur1.getName() : joueur2.getName();
        view.afficherScoreFinal(getNombreBornesControlees(1),getNombreBornesControlees(2),joueur1.getName(),joueur2.getName());
        view.afficherFinPartie(winner,nomGagnant);
    }
}