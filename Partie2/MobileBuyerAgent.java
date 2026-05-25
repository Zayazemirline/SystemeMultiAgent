package partie2;

import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.core.AID;
import jade.core.PlatformID;
import java.util.ArrayList;
import java.util.List;

public class MobileBuyerAgent extends Agent {

    private List<ProductOffer> offers = new ArrayList<>();

    private jade.core.AID[] sellers;
    private String[] containers;

    private int state = 0;

    private boolean remoteVisited = false;

    @Override
    protected void setup() {

        System.out.println(
                "\n[BUYER] START : " +
                here().getName()
        );

        addBehaviour(new FSMBehaviour());
    }

    @Override
    protected void beforeMove() {

        System.out.println(
                "\n[BUYER] Leaving : " +
                here().getName()
        );
    }

    @Override
    protected void afterMove() {

        System.out.println(
                "\n===================================="
        );

        System.out.println(
                "[BUYER] ARRIVED IN : " +
                here().getName()
        );

        System.out.println(
                "====================================\n"
        );

        try {
            Thread.sleep(5000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        addBehaviour(new FSMBehaviour());
    }

    private class FSMBehaviour extends OneShotBehaviour {

        @Override
        public void action() {

            // ========= DISCOVERY =========
            if(state == 0) {

                discover();

                state = 1;

                moveNext();

                return;
            }

            // ========= VISIT LOCAL SELLERS =========
            int idx = state - 1;

            if(idx < sellers.length) {

                collectOffer(sellers[idx]);

                state++;

                moveNext();

                return;
            }

            // ========= FINAL =========
            System.out.println("\n========== FINAL ==========");

            ProductOffer best =
                    SAWDecision.bestOffer(offers);

            System.out.println(best);

            doDelete();
        }

        private void discover() {

            try {

                DFAgentDescription template =
                        new DFAgentDescription();

                ServiceDescription sd =
                        new ServiceDescription();

                sd.setType("product-seller");

                template.addServices(sd);

                DFAgentDescription[] result =
                        DFService.search(myAgent, template);

                sellers =
                        new jade.core.AID[result.length];

                containers =
                        new String[result.length];

                for(int i = 0; i < result.length; i++) {

                    sellers[i] = result[i].getName();

                    containers[i] =
                            "Container-" + (i + 1);

                    System.out.println(
                            "[BUYER] Found : " +
                            sellers[i].getLocalName()
                    );
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        private void moveNext() {

            try {

                System.out.println(
                        "\n[BUYER] Waiting 5 sec..."
                );

                Thread.sleep(5000);

            } catch(Exception e) {
                e.printStackTrace();
            }

            int idx = state - 1;

            // ========= LOCAL MOVES =========
            if(idx < containers.length) {

                System.out.println(
                        "\n[BUYER] MOVING TO : " +
                        containers[idx]
                );

                try {
                    Thread.sleep(4000);
                } catch(Exception ignored){}

                doMove(
                        new ContainerID(
                                containers[idx],
                                null
                        )
                );

                return;
            }

            // ========= INTER PLATFORM =========
            if(!remoteVisited) {

                remoteVisited = true;

                System.out.println(
                        "\n===================================="
                );

                System.out.println(
                        "[BUYER] INTER-PLATFORM MOVE"
                );

                System.out.println(
                        "[BUYER] DESTINATION : PLATFORM 2"
                );

                System.out.println(
                        "[BUYER] MOVE IN 10 SECONDS..."
                );

                System.out.println(
                        "===================================="
                );

                try {
                    Thread.sleep(10000);
                } catch(Exception ignored){}

                AID remoteAmm =
                        new AID(
                                "ams@127.0.0.1:8888/JADE",
                                AID.ISGUID
                        );

                remoteAmm.addAddresses(
                        "http://127.0.0.1:7779/acc"
                );

                PlatformID destination =
                        new PlatformID(remoteAmm);

                doMove(destination);

                return;
            }

            // ========= END =========
            doDelete();
        }

        private void collectOffer(jade.core.AID seller) {

            try {

                ACLMessage msg =
                        new ACLMessage(ACLMessage.CFP);

                msg.addReceiver(seller);

                msg.setContent("offer");

                send(msg);

                MessageTemplate mt =
                        MessageTemplate.MatchSender(seller);

                ACLMessage reply =
                        blockingReceive(mt, 5000);

                if(reply != null) {

                    ProductOffer offer =
                            (ProductOffer)
                                    reply.getContentObject();

                    offers.add(offer);

                    System.out.println(
                            "[BUYER] Offer : " + offer
                    );
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}