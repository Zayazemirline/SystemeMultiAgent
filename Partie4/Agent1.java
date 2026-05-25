package partie4;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


/**
 * Agent1 (Tom)
 * Executes Subplan1: S3 < S2 < S1
 * After S3, sends synchronization message send(clear(F)) to Agent2
 */
public class Agent1 extends Agent {

    private State state;

    @Override
    protected void setup() {
        System.out.println("[Agent1/Tom] Ready. Waiting for subplan...");
        state = Planner.buildInitialState(); // shared world state (copy)
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

                System.out.println("\n[Agent1/Tom] ──── Executing Subplan1 ────");

                for (String step : steps) {
                    String[] parts = step.split(":", 2);
                    String stepId = parts[0];
                    String action = parts[1];

                    System.out.println("[Agent1/Tom] Executing " + stepId + ": " + action);
                    simulateAction(stepId, action);

                    // After S3: send synchronization (clear(F) is now true)
                    if (stepId.equals("S3")) {
                        System.out.println("[Agent1/Tom] → SEND(clear(F)) to Agent2 [SYNC]");
                        ACLMessage sync = new ACLMessage(ACLMessage.INFORM);
                        sync.addReceiver(new AID("Agent2", AID.ISLOCALNAME));
                        sync.setContent("SYNC:clear(F)");
                        myAgent.send(sync);
                    }
                }

                // Notify planner
                ACLMessage done = new ACLMessage(ACLMessage.INFORM);
                done.addReceiver(msg.getSender());
                done.setContent("DONE: Subplan1 [S3<S2<S1] completed");
                myAgent.send(done);

                System.out.println("[Agent1/Tom] ✓ Subplan1 complete.");
                doDelete();
            } else {
                block();
            }
        }

        private void simulateAction(String stepId, String action) {
            // Simulate execution with precondition check messages
            switch (stepId) {
                case "S3":
                    System.out.println("           Precond: on(E,F)=" + state.contains("on(E,F)")
                        + " clear(E)=" + state.contains("clear(E)") + " → OK");
                    state.remove("on(E,F)");
                    state.add("on(E,T)");
                    state.add("clear(F)");
                    System.out.println("           Effect:  on(E,T) added, clear(F) added ✓");
                    break;
                case "S2":
                    System.out.println("           Precond: on(A,B)=" + state.contains("on(A,B)")
                        + " clear(A)=" + state.contains("clear(A)")
                        + " clear(E)=" + state.contains("on(E,T)") + " → OK");
                    state.remove("on(A,B)");
                    state.add("on(A,E)");
                    state.add("clear(B)");
                    System.out.println("           Effect:  on(A,E) added, clear(B) added ✓");
                    break;
                case "S1":
                    System.out.println("           Precond: on(B,T)=" + state.contains("on(B,T)")
                        + " clear(B)=" + state.contains("clear(B)")
                        + " clear(A)=" + state.contains("clear(A)") + " → OK");
                    state.remove("on(B,T)");
                    state.add("on(B,A)");
                    System.out.println("           Effect:  on(B,A) added ✓ ← GOAL ACHIEVED");
                    break;
            }
        }
    }
}

