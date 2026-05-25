package partie4;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class MainContainer {

    public static void main(String[] args) throws Exception {
        Runtime rt = Runtime.instance();

        // Create main container
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.GUI, "true"); // set "false" to hide JADE GUI

        AgentContainer container = rt.createMainContainer(profile);

        // Launch Agent1 (Tom) first — will wait for messages
        AgentController agent1 = container.createNewAgent(
            "Agent1", "partie4.Agent1", new Object[]{});
        agent1.start();

        // Launch Agent2 (Bill)
        AgentController agent2 = container.createNewAgent(
            "Agent2", "partie4.Agent2", new Object[]{});
        agent2.start();

        // Small delay so agents are ready before planner starts
        Thread.sleep(500);

        // Launch PlannerAgent — orchestrates everything
        AgentController planner = container.createNewAgent(
            "PlannerAgent", "partie4.PlannerAgent", new Object[]{});
        planner.start();
    }
}
