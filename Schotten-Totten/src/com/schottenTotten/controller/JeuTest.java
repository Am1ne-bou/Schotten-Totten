package com.schottenTotten.controller;

public class JeuTest {
    public static void main(String[] args) {
        Jeu jeu = new Jeu(VarianteJeu.BASE,"1111", true, "2222", true);
        jeu.Gameloop();

    }
}