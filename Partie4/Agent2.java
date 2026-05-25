package partie4;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Agent2 (Bill)
 * Executes Subplan2: S6 < S5 < wait(clear(F)) < S4
 * Waits for synchronization from Agent1 before executing S4
 */
public class Agent2 extends Agent {

    private State state;

    @Override
    protected void setup() {
        System.out.println("[Agent2/Bill] Ready. Waiting for subplan...");
        state = Planner.buildInitialState();
        addBehaviour(new ExecuteBehaviour());
    }

    class ExecuteBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.blockingReceive(mt);

            if (msg != null && msg.getContent().startsWith("SUBPLAN:")) {
                String content = msg.getContent().substring("SUBPLAN:".length());
                String[] steps = content.split("\\|");
                AID plannerSender = msg.getSender();

                System.out.println("\n[Agent2/Bill] ──── Executing Subplan2 ────");

                for (String step : steps) {
                    if (step.startsWith("WAIT:")) {
                        String token = step.substring(5);
                        System.out.println("[Agent2/Bill] ⏳ WAIT(" + token + ") — blocking until Agent1 sends sync...");
                        waitForSync(token);
                        System.out.println("[Agent2/Bill] ✓ Sync received: " + token + " is now true. Resuming.");
                        continue;
                    }
                    String[] parts = step.split(":", 2);
                    String stepId = parts[0];
                    String action = parts[1];
                    System.out.println("[Agent2/Bill] Executing " + stepId + ": " + action);
                    simulateAction(stepId, action);
                }

                // Notify planner
                ACLMessage done = new ACLMessage(ACLMessage.INFORM);
                done.addReceiver(plannerSender);
                done.setContent("DONE: Subplan2 [S6<S5<wait<S4] completed");
                myAgent.send(done);

                System.out.println("[Agent2/Bill] ✓ Subplan2 complete.");
                doDelete();
            } else {
                block();
            }
        }

        private void waitForSync(String token) {
            // Block until we receive SYNC:clear(F) from Agent1
            MessageTemplate syncMt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchContent("SYNC:" + token)
            );
            myAgent.blockingReceive(syncMt, 30000);
            // Update local state
            state.add(token);
        }

        private void simulateAction(String stepId, String action) {
            switch (stepId) {
                case "S6":
                    System.out.println("           Precond: on(C,D)=" + state.contains("on(C,D)")
                        + " clear(C)=" + state.contains("clear(C)") + " → OK");
                    state.remove("on(C,D)");
                    state.add("on(C,T)");
                    state.add("clear(D)");
                    System.out.println("           Effect:  on(C,T) added, clear(D) added ✓");
                    break;
                case "S5":
                    System.out.println("           Precond: on(D,T)=" + state.contains("on(D,T)")
                        + " clear(D)=" + state.contains("clear(D)")
                        + " clear(C)=" + state.contains("on(C,T)") + " → OK");
                    state.remove("on(D,T)");
                    state.add("on(D,C)");
                    System.out.println("           Effect:  on(D,C) added ✓");
                    break;
                case "S4":
                    System.out.println("           Precond: on(F,T)=" + state.contains("on(F,T)")
                        + " clear(F)=" + state.contains("clear(F)")
                        + " clear(D)=" + state.contains("clear(D)") + " → OK");
                    state.remove("on(F,T)");
                    state.add("on(F,D)");
                    System.out.println("           Effect:  on(F,D) added ✓ ← GOAL ACHIEVED");
                    break;
            }
        }
    }
}
