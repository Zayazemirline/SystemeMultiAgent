package partie2;

import java.io.Serializable;

/**
 * Représente une offre d'un vendeur pour un produit donné.
 * Doit être Serializable pour que l'agent mobile puisse l'emporter avec lui.
 *
 * Critères multi-critères utilisés dans la décision SAW :
 *   - prix        (à minimiser → on inverse pour le score)
 *   - qualité     (à maximiser, échelle 1-10)
 *   - délai livraison en jours (à minimiser → on inverse)
 */
public class ProductOffer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sellerName;   // nom de l'agent vendeur
    private double price;        // prix proposé (€)
    private double quality;      // qualité sur 10
    private int    deliveryDays; // délai de livraison (jours)

    public ProductOffer(String sellerName, double price, double quality, int deliveryDays) {
        this.sellerName   = sellerName;
        this.price        = price;
        this.quality      = quality;
        this.deliveryDays = deliveryDays;
    }

    // --- Getters ---
    public String getSellerName()   { return sellerName;   }
    public double getPrice()        { return price;        }
    public double getQuality()      { return quality;      }
    public int    getDeliveryDays() { return deliveryDays; }

    @Override
    public String toString() {
        return String.format("[%s] prix=%.1f€  qualité=%.1f/10  délai=%dj",
                sellerName, price, quality, deliveryDays);
    }
}
