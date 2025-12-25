# Schotten-Totten

Jeu de cartes Schotten-Totten en Java - Projet PROG1 ENSEIRB-MATMECA

## Description

Application console permettant de jouer au jeu de société Schotten-Totten créé par Reiner Knizia. Deux joueurs s'affrontent pour le contrôle de 9 bornes en posant des cartes pour former des combinaisons.

## Fonctionnalités

- 4 variantes de jeu : Base, Tactique, Express, Express Tactique
- Mode Humain vs Humain, Humain vs IA, IA vs IA
- Intelligence artificielle (stratégie aléatoire)
- 10 cartes tactiques (Joker, Espion, Porte-Bouclier, Colin-Maillard, Combat de Boue, Chasseur de Tête, Stratège, Banshee, Traître)
- Affichage console coloré (codes ANSI)
- Vérification automatique des combinaisons et conditions de victoire

## Prérequis

- Java JDK 17 ou supérieur

## Compilation

```bash
cd src
javac com/schottenTotten/*.java com/schottenTotten/**/*.java
```

## Exécution

```bash
cd src
java com.schottenTotten.Main
```

## Structure du projet

```
src/com/schottenTotten/
├── Main.java
├── model/
│   ├── Joueur.java
│   ├── Borne.java
│   ├── Plateau.java
│   ├── carte/
│   │   ├── Carte.java
│   │   ├── CarteClan.java
│   │   └── CarteTactique.java
│   ├── decks/
│   │   ├── Deck.java
│   │   ├── DeckClan.java
│   │   └── DeckTactique.java
│   └── enums/
│       ├── Couleur.java
│       └── TypeCarteTactique.java
├── controller/
│   ├── IControleurJeu.java
│   ├── Jeu.java (+ GestionTactique)
│   ├── JeuFactory.java
│   └── VarianteJeu.java
├── view/
│   └── ConsoleView.java
├── ai/
│   └── IAAleatoire.java
└── utils/
    └── Constants.java
```

## Règles du jeu

### Variante de base
- 54 cartes clan (6 couleurs × 9 valeurs)
- 9 bornes à conquérir
- 6 cartes en main
- Combinaisons : Suite Couleur > Brelan > Couleur > Suite > Somme
- Victoire : 5 bornes OU 3 bornes consécutives

### Variante tactique
Ajoute 10 cartes spéciales :
- **Troupes d'élite** : Joker (×2), Espion, Porte-Bouclier
- **Modes de combat** : Colin-Maillard, Combat de Boue
- **Ruses** : Chasseur de Tête, Stratège, Banshee, Traître

## Auteur

Boussenna Mohamed Amine - ENSEIRB-MATMECA 2025-2026