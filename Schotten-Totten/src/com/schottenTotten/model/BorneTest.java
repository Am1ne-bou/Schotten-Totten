package com.schottenTotten.model;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;



public class BorneTest {
        private Borne borne;

        @Before
        public void setUp() {
            borne = new Borne();
        }

        @Test
        public void testInitialState() {
            assertFalse(borne.isLocked());
            assertEquals(0, borne.getProprietaire());
            assertTrue(borne.getCartesJ1().isEmpty());
            assertTrue(borne.getCartesJ2().isEmpty());
        }

        @Test
        public void testAddCarteJoueur1() {
            Carte carte = new Carte(Couleur.NOIR, 1);
            borne.addCarte(1, carte);
            assertEquals(1, borne.getCartesJ1().size());
            assertEquals(carte, borne.getCartesJ1().get(0));
        }

        @Test
        public void testAddCarteJoueur2() {
            Carte carte = new Carte(Couleur.BLEU, 2);
            borne.addCarte(2, carte);
            assertEquals(1, borne.getCartesJ2().size());
            assertEquals(carte, borne.getCartesJ2().get(0));
        }

        @Test
        public void testIsFull() {
            borne.addCarte(1, new Carte(Couleur.NOIR, 1));
            borne.addCarte(1, new Carte(Couleur.NOIR, 1));
            borne.addCarte(1, new Carte(Couleur.NOIR, 1));
            borne.addCarte(2, new Carte(Couleur.BLEU, 2));
            borne.addCarte(2, new Carte(Couleur.BLEU, 2));
            borne.addCarte(2, new Carte(Couleur.BLEU, 3));
            assertTrue(borne.isFull());
        }

        @Test
        public void testCompareCartes() {
            borne.addCarte(1, new Carte(Couleur.NOIR, 1));
            borne.addCarte(1, new Carte(Couleur.NOIR, 1));
            borne.addCarte(1, new Carte(Couleur.NOIR, 1));
            borne.addCarte(2, new Carte(Couleur.BLEU, 2));
            borne.addCarte(2, new Carte(Couleur.BLEU, 2));
            borne.addCarte(2, new Carte(Couleur.BLEU, 3));
            assertEquals(1, borne.compareCartes());
            assertEquals(1, borne.getProprietaire());
            assertTrue(borne.isLocked());
        }

        @Test
        public void testIsOwned() {
            borne.addCarte(1, new Carte(Couleur.NOIR, 1));
            borne.addCarte(1, new Carte(Couleur.NOIR, 1));
            borne.addCarte(1, new Carte(Couleur.NOIR, 1));
            borne.addCarte(2, new Carte(Couleur.BLEU, 2));
            borne.addCarte(2, new Carte(Couleur.BLEU, 2));
            borne.addCarte(2, new Carte(Couleur.BLEU, 3));
            borne.compareCartes();
            assertTrue(borne.isOwned());
        }

        @Test
        public void testTypeOfHandSomme() {
            List<Carte> hand = new ArrayList<>();
            hand.add(new Carte(Couleur.NOIR, 1));
            hand.add(new Carte(Couleur.BLEU, 3));
            hand.add(new Carte(Couleur.ROUGE, 5));
            assertEquals(0, borne.TypeofHand(hand)); // somme
        }

        @Test
        public void testTypeOfHandSuite() {
            List<Carte> hand = new ArrayList<>();
            hand.add(new Carte(Couleur.NOIR, 3));
            hand.add(new Carte(Couleur.BLEU, 4));
            hand.add(new Carte(Couleur.ROUGE, 2));
            assertEquals(1, borne.TypeofHand(hand)); // suite
        }

        @Test
        public void testTypeOfHandCouleur() {
            List<Carte> hand = new ArrayList<>();
            hand.add(new Carte(Couleur.VERT, 1));
            hand.add(new Carte(Couleur.VERT, 3));
            hand.add(new Carte(Couleur.VERT, 5));
            assertEquals(2, borne.TypeofHand(hand)); // couleur
        }

        @Test
        public void testTypeOfHandBrelan() {
            List<Carte> hand = new ArrayList<>();
            hand.add(new Carte(Couleur.BLEU, 4));
            hand.add(new Carte(Couleur.NOIR, 4));
            hand.add(new Carte(Couleur.ROUGE, 4));
            assertEquals(3, borne.TypeofHand(hand)); // brelan
        }

        @Test
        public void testTypeOfHandSuiteCouleur() {
            List<Carte> hand = new ArrayList<>();
            hand.add(new Carte(Couleur.ROUGE, 5));
            hand.add(new Carte(Couleur.ROUGE, 6));
            hand.add(new Carte(Couleur.ROUGE, 7));
            assertEquals(4, borne.TypeofHand(hand)); // suite couleur
        }

        @Test
        public void testCompareCartesTieGoesToLastPlayer() {
            // both brelan of value 2 -> same type and same sum -> owner should be lastPlayer (player 2)
            borne.addCarte(1, new Carte(Couleur.NOIR, 2));
            borne.addCarte(1, new Carte(Couleur.BLEU, 2));
            borne.addCarte(1, new Carte(Couleur.VERT, 2));
            borne.addCarte(2, new Carte(Couleur.ROUGE, 2));
            borne.addCarte(2, new Carte(Couleur.NOIR, 2));
            borne.addCarte(2, new Carte(Couleur.BLEU, 2)); // lastPlayer = 2
            assertEquals(2, borne.compareCartes());
            assertEquals(2, borne.getProprietaire());
            assertTrue(borne.isLocked());
        }

        @Test(expected = IllegalStateException.class)
        public void testCompareCartesThrowsWhenNotFull() {
            borne.addCarte(1, new Carte(Couleur.NOIR, 1));
            borne.addCarte(2, new Carte(Couleur.BLEU, 2));
            // not full yet -> should throw
            borne.compareCartes();
        }
        
        @Test(expected = IllegalStateException.class)
        public void testAddCarteJoueur1LimitExceeded() {
            borne.addCarte(1, new Carte(Couleur.NOIR, 1));
            borne.addCarte(1, new Carte(Couleur.NOIR, 2));
            borne.addCarte(1, new Carte(Couleur.NOIR, 3));
            // fourth should throw
            borne.addCarte(1, new Carte(Couleur.NOIR, 4));
        }

        @Test(expected = IllegalStateException.class)
        public void testAddCarteJoueur2LimitExceeded() {
            borne.addCarte(2, new Carte(Couleur.BLEU, 1));
            borne.addCarte(2, new Carte(Couleur.BLEU, 2));
            borne.addCarte(2, new Carte(Couleur.BLEU, 3));
            // fourth should throw
            borne.addCarte(2, new Carte(Couleur.BLEU, 4));
        }

        @Test(expected = IllegalArgumentException.class)
        public void testAddCarteInvalidPlayer() {
            borne.addCarte(3, new Carte(Couleur.ROUGE, 1));
        }

        @Test
        public void testCompareCartesSumTieGoesToLastPlayer1() {
            // ensure both hands are type "somme" and sums equal; lastPlayer should be 1
            borne.addCarte(2, new Carte(Couleur.ROUGE, 1));
            borne.addCarte(2, new Carte(Couleur.BLEU, 3));
            borne.addCarte(2, new Carte(Couleur.VERT, 5)); // J2 sum = 9, type 0
            borne.addCarte(1, new Carte(Couleur.NOIR, 2));
            borne.addCarte(1, new Carte(Couleur.BLEU, 2));
            borne.addCarte(1, new Carte(Couleur.ROUGE, 5)); // J1 sum = 9, type 0, lastPlayer = 1
            assertEquals(1, borne.compareCartes());
            assertEquals(1, borne.getProprietaire());
            assertTrue(borne.isLocked());
        }

        @Test
        public void testCompareCartesCouleurSumDecidesWinner() {
            // both "couleur" but different sums -> higher sum wins
            borne.addCarte(1, new Carte(Couleur.VERT, 1));
            borne.addCarte(1, new Carte(Couleur.VERT, 3));
            borne.addCarte(1, new Carte(Couleur.VERT, 5)); // sum 9, type 2
            borne.addCarte(2, new Carte(Couleur.ROUGE, 2));
            borne.addCarte(2, new Carte(Couleur.ROUGE, 4));
            borne.addCarte(2, new Carte(Couleur.ROUGE, 6)); // sum 12, type 2
            assertEquals(2, borne.compareCartes());
            assertEquals(2, borne.getProprietaire());
        }

        @Test(expected = IndexOutOfBoundsException.class)
        public void testTypeOfHandInvalidSizeThrows() {
            List<Carte> hand = new ArrayList<>();
            hand.add(new Carte(Couleur.NOIR, 1));
            hand.add(new Carte(Couleur.BLEU, 2));
            // only two cards -> should throw when accessing index 2
            borne.TypeofHand(hand);
        }

        @Test
        public void testSettersModifyState() {
            borne.setProprietaire(2);
            borne.setLocked(true);
            assertEquals(2, borne.getProprietaire());
            assertTrue(borne.isLocked());
            assertTrue(borne.isOwned());
        }

    
}
