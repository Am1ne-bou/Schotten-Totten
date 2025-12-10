package com.schottenTotten.model;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;


public class JoueurTest {
    private Joueur joueur;
    private List<Carte> cartes;

    @Before
    public void setUp() {
        joueur = new Joueur("Alice", false);
        cartes = new ArrayList<>();
        cartes.add(new Carte(Couleur.BLEU, 5));
        cartes.add(new Carte(Couleur.NOIR, 2));
    }

    @Test
    public void testJoueurCreation() {
        assertEquals("Alice", joueur.getName());
        assertFalse(joueur.isAI());
        assertTrue(joueur.getHand().isEmpty());
    }

    @Test
    public void testAddCarteToHand() {
        joueur.addCarteToHand(cartes);
        assertEquals(2, joueur.getHand().size());
    }

    @Test
    public void testAddCarteToHandNull() {
        assertThrows(IllegalArgumentException.class, () -> joueur.addCarteToHand(null));
    }

    @Test
    public void testAddCarteToHandEmpty() {
        assertThrows(IllegalArgumentException.class, () -> joueur.addCarteToHand(new ArrayList<>()));
    }

    @Test
    public void testRemoveCarteFromHand() {
        joueur.addCarteToHand(cartes);
        joueur.removeCarteFromHand(cartes.get(0));
        assertEquals(1, joueur.getHand().size());
    }

    @Test
    public void testRemoveCarteFromHandNotFound() {
        joueur.addCarteToHand(cartes);
        Carte carteNotInHand = new Carte(Couleur.ROUGE, 3);
        assertThrows(IllegalArgumentException.class, () -> joueur.removeCarteFromHand(carteNotInHand));
    }

}