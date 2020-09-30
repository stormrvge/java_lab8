package sample.commands;

import sample.connection.client.Client;
import sample.connection.server.Server;
import sample.logic.Packet;
import sample.logic.User;

public class CountByDistanceCmd extends Command {

    public CountByDistanceCmd() {
        super("Count By Distance", true, false);
    }

    public boolean getRequireLogin() {
        return require_login;
    }

    @Override
    public Packet execOnClient(Client client, Object ... args) {
        if (client.getUser().getLoginState()) {
            return new Packet(this, args[0], client.getUser());
        } else {
            System.err.println("You must login!");
            return null;
        }
    }

    @Override
    public Packet execOnServer(Server server, Object args, User user) {
        float distance = (float) args;
        return new Packet(null, server.getManager().count_by_distance(distance), null);
    }
}
