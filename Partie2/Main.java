package partie2;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;

public class Main {

    public static void main(String[] args) throws Exception {

        Runtime.instance().setCloseVM(true);

        Runtime rt = Runtime.instance();

        ProfileImpl mainProfile = new ProfileImpl(false);

        mainProfile.setParameter(Profile.MAIN, "true");
        mainProfile.setParameter(Profile.GUI, "true");

        mainProfile.setParameter(Profile.LOCAL_HOST, "127.0.0.1");
        mainProfile.setParameter(Profile.LOCAL_PORT, "1099");

        mainProfile.setParameter(
                Profile.PLATFORM_ID,
                "127.0.0.1:1099/JADE"
        );

        mainProfile.setParameter(
                Profile.SERVICES,
                "jade.core.mobility.AgentMobilityService;" +
                "jade.core.migration.InterPlatformMobilityService"
        );

        mainProfile.setParameter(
                Profile.MTPS,
                "jade.mtp.http.MessageTransportProtocol(http://127.0.0.1:7778/acc)"
        );

        AgentContainer main =
                rt.createMainContainer(mainProfile);

        // ================= CONTAINER 1 =================

        ProfileImpl c1Profile = new ProfileImpl(false);

        c1Profile.setParameter(
                Profile.CONTAINER_NAME,
                "Container-1"
        );

        c1Profile.setParameter(
                Profile.MAIN_HOST,
                "127.0.0.1"
        );

        c1Profile.setParameter(
                Profile.MAIN_PORT,
                "1099"
        );

        AgentContainer c1 =
                rt.createAgentContainer(c1Profile);

        // ================= CONTAINER 2 =================

        ProfileImpl c2Profile = new ProfileImpl(false);

        c2Profile.setParameter(
                Profile.CONTAINER_NAME,
                "Container-2"
        );

        c2Profile.setParameter(
                Profile.MAIN_HOST,
                "127.0.0.1"
        );

        c2Profile.setParameter(
                Profile.MAIN_PORT,
                "1099"
        );

        AgentContainer c2 =
                rt.createAgentContainer(c2Profile);

        // ================= SELLERS =================

        c1.createNewAgent(
                "Seller1",
                "partie2.SellerAgent",
                new Object[]{120.0, 8.0, 3}
        ).start();

        c2.createNewAgent(
                "Seller2",
                "partie2.SellerAgent",
                new Object[]{95.0, 6.0, 7}
        ).start();

        Thread.sleep(2000);

        // ================= BUYER =================

        main.createNewAgent(
                "Buyer",
                "partie2.MobileBuyerAgent",
                null
        ).start();
    }
}