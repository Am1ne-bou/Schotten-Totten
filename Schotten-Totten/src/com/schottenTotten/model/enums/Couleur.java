package com.schottenTotten.model.enums;

import java.util.List;

import com.schottenTotten.model.carte.Carte;
import com.schottenTotten.model.carte.CarteTactique;
import com.schottenTotten.utils.*;

public enum Couleur {
    ROUGE("R",Constants.ROUGE),
    BLEU("B", Constants.BLEU),
    VERT("V", Constants.VERT),
    JAUNE("J", Constants.JAUNE),
    ORANGE("O", Constants.ORANGE),
    VIOLET("P",Constants.MAGENTA);

    private final String abreviation;
    private final String code;

    Couleur(String abreviation,String code) {
        this.abreviation = abreviation;
        this.code = code;
    }

    public String getAbreviation() {
        return abreviation;
    }

    public String getCode() {
        return code;
    }

    public String getCodeFromCouleur(Couleur couleur){
        return couleur.getCode();
    }

    public String getCouleurCode(Couleur couleur) {
        if (couleur == null) return Constants.CYAN;
        return switch (couleur) {
            case ROUGE -> ROUGE.getCode();
            case BLEU -> BLEU.getCode();
            case VERT -> VERT.getCode();
            case JAUNE -> JAUNE.getCode();
            case ORANGE -> ORANGE.getCode();
            case VIOLET -> VIOLET.getCode();
        };
    }

    public String colorerCarte(Carte carte) {
        if (carte == null) return Constants.GRIS + "[   ]" + Constants.RESET;
        if (carte.isTactique()) {
            return Constants.CYAN + Constants.BOLD + "[" + carte.getValeur() + "-" + ((CarteTactique) carte).getType().toString().substring(0, 5) + "]" + Constants.RESET;
        }
        String couleurCode = getCouleurCode(carte.getCouleur());
        return couleurCode + Constants.BOLD + "[" + carte.getValeur() + "-" + carte.getCouleur().toString().substring(0, 2) + "]" + Constants.RESET;
    }

    public String colorerCartes(List<Carte> cartes) {
        if (cartes.isEmpty()) return Constants.GRIS + "[     ]" + Constants.RESET;
        StringBuilder sb = new StringBuilder();
        for (Carte carte : cartes) sb.append(colorerCarte(carte)).append(" ");
        return sb.toString();
    }
}