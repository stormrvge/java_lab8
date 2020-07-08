package sample.commands;

import sample.connection.client.Client;
import sample.connection.server.Server;
import sample.logic.Packet;
import sample.logic.User;
import sample.logic.collectionClasses.Route;

public class UpdateIdCmd extends Command {
    public UpdateIdCmd() {
        super("update id", true);
    }

    public boolean getRequireLogin() {
        return require_login;
    }

    public Packet execOnServer(Server server, Object object, User user) {
        Object[] objects = (Object[]) object;
        return new Packet(null, server.getManager().update_id((Integer) objects[0],
                (Route) objects[1], server, user), null);
    }

    @Override
    public Packet execOnClient(Client client, Object ... args) {
        try {
            if (client.getUser().getLoginState()) {

                Object[] objects = new Object[] {args[0], args[1]};
                return new Packet(this, objects, client.getUser());
            } else {
                System.err.println("You must login!");
                return null;
            }
        } catch (NumberFormatException e) {
            System.err.println("Wrong format for argument.");
            return null;
        }
    }
}
