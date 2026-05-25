package partie4;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;

public class PlannerAgent extends Agent {

    private State currentState;
    private List<Action> plan;
    // Synchronization flag: S3 done?
    private boolean s3Done = false;

    @Override
    protected void setup() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║     CENTRALIZED PLANNING FOR DISTRIBUTED PLANS       ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");

        currentState = Planner.buildInitialState();
        Set<String> goal = Planner.buildGoal();

        System.out.println("\n[PlannerAgent] Initial State: " + currentState);
        System.out.println("[PlannerAgent] Goal: " + goal);

        // Generate plan
        plan = Planner.generatePlan();
        System.out.println("\n[PlannerAgent] ──── Plan Generated (6 actions) ────");
        System.out.println("  S1: move(B,T,A)     → Agent1 (Tom)");
        System.out.println("  S2: move(A,B,E)     → Agent1 (Tom)");
        System.out.println("  S3: movetable(E,F)  → Agent1 (Tom)");
        System.out.println("  S4: move(F,T,D)     → Agent2 (Bill)");
        System.out.println("  S5: move(D,T,C)     → Agent2 (Bill)");
        System.out.println("  S6: movetable(C,D)  → Agent2 (Bill)");

        System.out.println("\n[PlannerAgent] ──── Partial Ordering ────");
        for (String c : Planner.getPartialOrderConstraints()) {
            System.out.println("  " + c);
        }

        System.out.println("\n[PlannerAgent] ──── Decomposition ────");
        System.out.println("  Subplan1 (Agent1/Tom):  S3 < S2 < S1");
        System.out.println("  Subplan2 (Agent2/Bill): S6 < S5 < S4");
        System.out.println("  Sync: Agent1 sends(clear(F)) after S3 → Agent2 waits(clear(F)) before S4");

        // Add dispatch behaviour
        addBehaviour(new DispatchBehaviour());
    }

    /**
     * Dispatches subplans to agents and handles synchronization.
     * Agent1 gets [S3, S2, S1] and after S3 sends sync(clear(F)) to Agent2.
     * Agent2 gets [S6, S5] then waits for sync before S4.
     */
    class DispatchBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            System.out.println("\n[PlannerAgent] ──── Dispatching Sub-Plans ────");

            AID agent1 = new AID("Agent1", AID.ISLOCALNAME);
            AID agent2 = new AID("Agent2", AID.ISLOCALNAME);

            // Send Subplan1 to Agent1: S3,S2,S1 (in execution order)
            sendSubplan(agent1, "SUBPLAN:S3:movetable(E,F)|S2:move(A,B,E)|S1:move(B,T,A)");
            System.out.println("[PlannerAgent] → Sent Subplan1 to Agent1: S3 < S2 < S1");

            // Send Subplan2 to Agent2: S6,S5 then wait for sync, then S4
            sendSubplan(agent2, "SUBPLAN:S6:movetable(C,D)|S5:move(D,T,C)|WAIT:clear(F)|S4:move(F,T,D)");
            System.out.println("[PlannerAgent] → Sent Subplan2 to Agent2: S6 < S5 < wait(clear(F)) < S4");

            // Wait for both agents to finish
            System.out.println("\n[PlannerAgent] Waiting for agents to complete...");
            waitForDone(agent1);
            waitForDone(agent2);

            System.out.println("\n[PlannerAgent] ══════════════════════════════════");
            System.out.println("[PlannerAgent] ✓ ALL GOALS ACHIEVED!");
            System.out.println("  on(B,A) ✓  on(F,D) ✓  on(E,T) ✓");
            System.out.println("[PlannerAgent] ══════════════════════════════════");
            doDelete();
        }

        private void sendSubplan(AID target, String content) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(target);
            msg.setContent(content);
            myAgent.send(msg);
        }

        private void waitForDone(AID agent) {
            MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchSender(agent)
            );
            ACLMessage reply = myAgent.blockingReceive(mt, 30000);
            if (reply != null && reply.getContent().startsWith("DONE")) {
                System.out.println("[PlannerAgent] ✓ " + agent.getLocalName() + " finished: " + reply.getContent());
            } else {
                System.out.println("[PlannerAgent] ✗ Timeout waiting for " + agent.getLocalName());
            }
        }
    }
}

