package com.schottenTotten.model;

import org.junit.Test;
import static org.junit.Assert.*;


public class PlateauTest {

    @Test
    public void testConstructor_initializesNineBornes() {
        Plateau p = new Plateau();
        for (int i = 0; i < 9; i++) {
            assertNotNull("Borne " + i + " should not be null", p.getBornes(i));
        }
    }

    @Test
    public void testGetBornes_invalidIndex_throws() {
        Plateau p = new Plateau();
        try {
            p.getBornes(-1);
            fail("Expected IllegalArgumentException for index -1");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            p.getBornes(9);
            fail("Expected IllegalArgumentException for index 9");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testGameEnded_byFiveBornesControlled() {
        Plateau p = new Plateau();
        // Give player 1 five bornes
        p.getBornes(0).proprietaire = 1;
        p.getBornes(1).proprietaire = 1;
        p.getBornes(2).proprietaire = 1;
        p.getBornes(3).proprietaire = 1;
        p.getBornes(4).proprietaire = 1;
        assertTrue("Game should be ended when player 1 controls 5 bornes", p.gameended());
        assertEquals("Winner should be player 1", 1, p.getwinner());
    }

    @Test
    public void testGameEnded_byThreeConsecutive() {
        Plateau p = new Plateau();
        // Give player 2 three consecutive bornes
        p.getBornes(2).proprietaire = 2;
        p.getBornes(3).proprietaire = 2;
        p.getBornes(4).proprietaire = 2;
        assertTrue("Game should be ended when player 2 controls 3 consecutive bornes", p.gameended());
        assertEquals("Winner should be player 2", 2, p.getwinner());
    }

    @Test
    public void testBothPlayersWinning_getWinnerReturnsFirst() {
        Plateau p = new Plateau();
        // Player 1 controls 0-2 (three consecutive)
        p.getBornes(0).proprietaire = 1;
        p.getBornes(1).proprietaire = 1;
        p.getBornes(2).proprietaire = 1;
        // Player 2 controls 3-5 (three consecutive)
        p.getBornes(3).proprietaire = 2;
        p.getBornes(4).proprietaire = 2;
        p.getBornes(5).proprietaire = 2;
        assertTrue("Game should be ended when both have winning patterns", p.gameended());
        // getwinner checks player 1 first, so should return 1
        assertEquals("When both win, getwinner should return 1", 1, p.getwinner());
    }

    public static void main(String[] args) {
        Plateau p = new Plateau();
        p.getBornes(5).getCartesJ1().add(new Carte(Couleur.ROUGE, 3));
        p.getBornes(5).getCartesJ1().add(new Carte(Couleur.NOIR, 3));
        p.getBornes(5).getCartesJ1().add(new Carte(Couleur.BLEU, 3));
        p.getBornes(5).getCartesJ2().add(new Carte(Couleur.ORANGE, 5));
        p.getBornes(5).getCartesJ2().add(new Carte(Couleur.ORANGE, 3));
        p.getBornes(5).getCartesJ2().add(new Carte(Couleur.ORANGE, 2));
        if(p.getBornes(5).compareCartes() == 1){
            p.getBornes(5).setLocked(true);
            p.getBornes(5).setProprietaire(1);
        }else if(p.getBornes(5).compareCartes() == 2){
            p.getBornes(5).setLocked(true);
            p.getBornes(5).setProprietaire(2);
        }
        System.out.println("Borne 5 owned by player: " + p.getBornes(5).getProprietaire());
        
    }
}
