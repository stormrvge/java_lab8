package sample.commands;

import sample.connection.client.Client;
import sample.connection.server.Server;
import sample.logic.Packet;
import sample.logic.User;

public class UniqueDistanceCmd extends Command {
    public UniqueDistanceCmd() {
        super("unique distance", true);
    }

    public boolean getRequireLogin() {
        return require_login;
    }

    @Override
    public Packet execOnClient(Client client, Object ... args) {
        if (client.getUser().getLoginState()) {
            return new Packet(this, args, client.getUser());
        } else {
            System.err.println("You must login!");
            return null;
        }
    }

    @Override
    public Packet execOnServer(Server server, Object args, User user) {
        return new Packet(null, server.getManager().print_unique_distance(), null);
    }
}