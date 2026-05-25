package partie2;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * AGENT VENDEUR (SellerAgent)
 * ----------------------------
 * Chaque vendeur possède une offre fixe (prix, qualité, délai).
 * Il attend un message CFP (Call For Proposal) de l'acheteur
 * et répond avec son offre encodée en Java-sérialisé.
 *
 * Lancement dans Eclipse (Run Configuration) :
 *   Main class : jade.Boot
 *   Arguments  : -gui -container -agents
 *                Seller1:agents.SellerAgent(120,8,3);Seller2:agents.SellerAgent(95,6,7)
 *
 * Les 3 arguments du constructeur (via getArguments()) :
 *   arg[0] = prix (double)
 *   arg[1] = qualité (double, 1-10)
 *   arg[2] = délai en jours (int)
 */
public class SellerAgent extends Agent {

    private double price;
    private double quality;
    private int    deliveryDays;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length == 3) {
            price        = Double.parseDouble(args[0].toString());
            quality      = Double.parseDouble(args[1].toString());
            deliveryDays = Integer.parseInt(args[2].toString());
        } else {
            // Valeurs par défaut si pas d'arguments
            price = 100; quality = 7; deliveryDays = 5;
        }

        System.out.println(getLocalName() + " démarré → prix=" + price
                + "€  qualité=" + quality + "  délai=" + deliveryDays + "j");

        // Enregistrement dans le DF (Pages Jaunes) pour que l'acheteur nous trouve
        try {
            jade.domain.DFService.register(this, buildDFDescription());
        } catch (jade.domain.FIPAException e) {
            e.printStackTrace();
        }

        addBehaviour(new AnswerBuyer());
    }

    // Construit la description pour le DF
    private jade.domain.FIPAAgentManagement.DFAgentDescription buildDFDescription() {
        jade.domain.FIPAAgentManagement.DFAgentDescription dfd =
                new jade.domain.FIPAAgentManagement.DFAgentDescription();
        dfd.setName(getAID());
        jade.domain.FIPAAgentManagement.ServiceDescription sd =
                new jade.domain.FIPAAgentManagement.ServiceDescription();
        sd.setType("product-seller");
        sd.setName("ProductSale");
        dfd.addServices(sd);
        return dfd;
    }

    @Override
    protected void takeDown() {
        try { jade.domain.DFService.deregister(this); }
        catch (Exception e) { /* ignore */ }
        System.out.println(getLocalName() + " arrêté.");
    }

    // ---------------------------------------------------------------
    // Behaviour : attente et réponse aux demandes de l'acheteur
    // ---------------------------------------------------------------
    private class AnswerBuyer extends CyclicBehaviour {

        // On ne répond qu'aux messages CFP (Call For Proposal)
        private final MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                System.out.println(getLocalName() + " reçoit une demande de " + msg.getSender().getLocalName());

                // Construire l'offre
                ProductOffer offer = new ProductOffer(
                        getLocalName(), price, quality, deliveryDays);

                // Répondre avec PROPOSE + l'objet sérialisé
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.PROPOSE);
                try {
                    reply.setContentObject(offer);  // sérialisation Java
                } catch (Exception e) {
                    e.printStackTrace();
                }
                myAgent.send(reply);
                System.out.println(getLocalName() + " envoie : " + offer);

            } else {
                block(); // Rien à faire, mise en attente passive (économie CPU)
            }
        }
    }
}
