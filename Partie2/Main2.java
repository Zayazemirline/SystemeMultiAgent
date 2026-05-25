package partie2;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;

public class Main2 {

    public static void main(String[] args) throws Exception {

        Runtime.instance().setCloseVM(true);

        Runtime rt = Runtime.instance();

        ProfileImpl p = new ProfileImpl(false);

        p.setParameter(Profile.MAIN, "true");
        p.setParameter(Profile.GUI, "true");

        p.setParameter(Profile.LOCAL_HOST, "127.0.0.1");
        p.setParameter(Profile.LOCAL_PORT, "8888");

        p.setParameter(
                Profile.PLATFORM_ID,
                "127.0.0.1:8888/JADE"
        );

        p.setParameter(
                Profile.SERVICES,
                "jade.core.mobility.AgentMobilityService;" +
                "jade.core.migration.InterPlatformMobilityService"
        );

        p.setParameter(
                Profile.MTPS,
                "jade.mtp.http.MessageTransportProtocol(http://127.0.0.1:7779/acc)"
        );

        AgentContainer main =
                rt.createMainContainer(p);

        main.createNewAgent(
                "RemoteSeller",
                "partie2.SellerAgent",
                new Object[]{80.0, 9.0, 2}
        ).start();

        System.out.println("\n[PLATFORM-2 READY]");
    }
}