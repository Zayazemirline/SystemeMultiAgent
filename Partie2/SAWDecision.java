package partie2;

import java.util.List;

/**
 * Méthode SAW (Simple Additive Weighting) — décision multi-critères.
 *
 * Principe :
 *   1. Normaliser chaque critère entre 0 et 1.
 *   2. Multiplier par le poids du critère.
 *   3. Sommer → score final. Le score le plus élevé gagne.
 *
 * Poids utilisés (doivent sommer à 1) :
 *   Prix        : 0.5  (critère le plus important)
 *   Qualité     : 0.3
 *   Délai       : 0.2
 */
public class SAWDecision {

    // Poids des critères (modifiables selon le contexte)
    private static final double W_PRICE   = 0.5;
    private static final double W_QUALITY = 0.3;
    private static final double W_DELAY   = 0.2;

    /**
     * Retourne l'offre gagnante parmi la liste.
     */
    public static ProductOffer bestOffer(List<ProductOffer> offers) {
        if (offers == null || offers.isEmpty()) return null;

        // --- Trouver min/max pour normalisation ---
        double minPrice = offers.stream().mapToDouble(ProductOffer::getPrice).min().getAsDouble();
        double maxPrice = offers.stream().mapToDouble(ProductOffer::getPrice).max().getAsDouble();

        double minQual  = offers.stream().mapToDouble(ProductOffer::getQuality).min().getAsDouble();
        double maxQual  = offers.stream().mapToDouble(ProductOffer::getQuality).max().getAsDouble();

        double minDel   = offers.stream().mapToDouble(ProductOffer::getDeliveryDays).min().getAsDouble();
        double maxDel   = offers.stream().mapToDouble(ProductOffer::getDeliveryDays).max().getAsDouble();

        ProductOffer best  = null;
        double       bestScore = -1;

        System.out.println("\n=== Calcul SAW ===");
        System.out.printf("%-20s %-10s %-10s %-10s %-10s%n",
                "Vendeur","normPrix","normQual","normDél","SCORE");

        for (ProductOffer o : offers) {
            // Prix : on veut le MIN → normalisation inversée
            double nPrice = (maxPrice == minPrice) ? 1.0
                    : (maxPrice - o.getPrice()) / (maxPrice - minPrice);

            // Qualité : on veut le MAX → normalisation directe
            double nQual  = (maxQual == minQual) ? 1.0
                    : (o.getQuality() - minQual) / (maxQual - minQual);

            // Délai : on veut le MIN → normalisation inversée
            double nDel   = (maxDel == minDel) ? 1.0
                    : (maxDel - o.getDeliveryDays()) / (maxDel - minDel);

            double score  = W_PRICE * nPrice + W_QUALITY * nQual + W_DELAY * nDel;

            System.out.printf("%-20s %-10.3f %-10.3f %-10.3f %-10.3f%n",
                    o.getSellerName(), nPrice, nQual, nDel, score);

            if (score > bestScore) {
                bestScore = score;
                best      = o;
            }
        }
        System.out.println("=================\n");
        return best;
    }
}
