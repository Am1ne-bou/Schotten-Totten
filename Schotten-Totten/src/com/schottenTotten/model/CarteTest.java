package com.schottenTotten.model;

import org.junit.Test;
import static org.junit.Assert.*;


public class CarteTest {

    @Test
    public void testCarteCreationValid() {
        Carte carte = new Carte(Couleur.ROUGE, 5);
        assertEquals(Couleur.ROUGE, carte.getCouleur());
        assertEquals(5, carte.getValeur());
    }

    @Test
    public void testCarteValeurTropBasse() {
        assertThrows(IllegalArgumentException.class, () -> new Carte(Couleur.NOIR, 0));
    }

    @Test
    public void testCarteValeurTropHaute() {
        assertThrows(IllegalArgumentException.class, () -> new Carte(Couleur.ORANGE, 10));
    }

    @Test
    public void testCarteToString() {
        Carte carte = new Carte(Couleur.ROUGE, 7);
        assertEquals("7-ROUGE", carte.toString());
    }

    @Test
    public void testCarteGetCouleur() {
        Carte carte = new Carte(Couleur.BLEU, 3);
        assertEquals(Couleur.BLEU, carte.getCouleur());
    }

    @Test
    public void testCarteGetValeur() {
        Carte carte = new Carte(Couleur.VERT, 4);
        assertEquals(4, carte.getValeur());
    }
}