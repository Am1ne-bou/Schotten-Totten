package com.schottenTotten.model;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;

public class DeckTest {
    private Deck deck;

    @Before
    public void setUp() {
        deck = new Deck();
    }

    @Test
    public void testDeckInitialization() {
        assertEquals("Deck should contain 54 cards (6 colors × 9 values)", deck.getDeckSize(), 54);
    }

    @Test
    public void testShuffle() {
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();
        deck1.shuffle();
        deck2.shuffle();
        assertNotEquals("Shuffled decks should have different order", deck1, deck2);
    }

    @Test
    public void testPiocher() {
        List<Carte> cartes = deck.piocher(5);
        assertEquals("Should draw 5 cards", 5, cartes.size());
        assertEquals("Deck should have 49 cards remaining", 49, deck.getDeckSize());
    }

    @Test
    public void testPiocherAll() {
        List<Carte> cartes = deck.piocher(54);
        assertEquals("Should draw all 54 cards", 54, cartes.size());
        assertEquals("Deck should be empty", 0, deck.getDeckSize());
    }

    @Test
    public void testPiocherEmptyDeck() {
        deck.piocher(54); // draw all cards
        assertThrows("Should throw exception when deck is empty", IllegalStateException.class, () -> deck.piocher(1));
    }

    @Test
    public void testPiocherMoreThanAvailable() {
        assertThrows("Should throw exception when drawing more cards than available", IllegalArgumentException.class, () -> deck.piocher(60));
    }

    @Test
    public void testMultiplePiocher() {
        deck.piocher(10);
        assertEquals(44, deck.getDeckSize());
        deck.piocher(5);
        assertEquals(39, deck.getDeckSize());
    }
}