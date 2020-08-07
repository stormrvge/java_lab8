package sample.commands;

import sample.connection.client.Client;
import sample.connection.server.Server;
import sample.logic.Packet;
import sample.logic.User;
import sample.logic.collectionClasses.Route;

public class AddCmd extends Command {

    public AddCmd() {
        super("add", true, true);
    }

    public boolean getRequireLogin() {
        return require_login;
    }

    public Packet execOnServer(Server server, Object object, User user) {
        return new Packet(null, null,
                null, server.getManager().add(server, (Route) object, user));
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
}
