package sample.commands;

import sample.connection.client.Client;
import sample.connection.server.Server;
import sample.logic.Packet;
import sample.logic.User;

import java.sql.SQLException;

public class LoadTableCmd extends Command {
    public LoadTableCmd() {
        super(true);
    }

    public boolean getRequireLogin() {
        return require_login;
    }

    @Override
    public Packet execOnClient(Client client, String ... args) {
        if (client.getUser().getLoginState()) {
            return new Packet(this, args, client.getUser());
        } else {
            System.err.println("You must login!");
            return null;
        }
    }

    @Override
    public Packet execOnServer(Server server, Object args, User user) {
        try {
            if (server.getSqlStatements().login(user)) {
                return new Packet(null, server.getManager().getRouteCollection(), null);
            } else {
                return new Packet(false);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new Packet(false);
        }


    }
}
