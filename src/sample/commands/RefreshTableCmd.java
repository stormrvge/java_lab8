package sample.commands;

import sample.connection.client.Client;
import sample.connection.server.Server;
import sample.logic.Packet;
import sample.logic.User;


public class RefreshTableCmd extends Command {
    public RefreshTableCmd() {
        super("refresh", true, false);
    }

    @Override
    public boolean getRequireLogin() {
        return true;
    }

    @Override
    public Packet execOnServer(Server server, Object args, User user) {
        System.out.println("SYNC CMD");

        Command loadTable = new LoadTableCmd();
        Packet packet = loadTable.execOnServer(server, args, user);
        return new Packet(packet.getArgument(), true);
    }

    @Override
    public Packet execOnClient(Client client, Object... args) {
        System.out.println("SYNC CMD");
        return null;
    }
}
