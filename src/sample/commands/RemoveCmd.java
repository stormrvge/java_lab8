package sample.commands;

import sample.connection.client.Client;
import sample.connection.server.Server;
import sample.logic.Packet;
import sample.logic.User;

public class RemoveCmd extends Command {
    public RemoveCmd() {
        super("remove", true, true);
    }

    public boolean getRequireLogin() {
        return require_login;
    }

    @Override
    public Packet execOnServer(Server server, Object args, User user) {
        return new Packet(null, server.getManager().remove_by_id(server, (int) args, user), null);
    }

    @Override
    public Packet execOnClient(Client client, Object ... args) {
        if (client.getUser().getLoginState()) {
            Integer id = (Integer) args[0];
            return new Packet(this, id, client.getUser());
        } else {
            System.out.println("You must login!");
            return null;
        }
    }
}
