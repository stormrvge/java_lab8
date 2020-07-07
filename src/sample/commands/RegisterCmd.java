package sample.commands;

import sample.connection.client.Client;
import sample.connection.server.Server;
import sample.logic.Packet;
import sample.logic.User;

import java.sql.SQLException;

public class RegisterCmd extends Command {

    public RegisterCmd() {
        super(false);
    }

    public boolean getRequireLogin() {
        return require_login;
    }

    public Packet execOnClient(Client client, Object ... args) {
        if (args.length != 2) {
            System.err.println("You cant take " + (args.length - 1) + " arguments");
            return null;
        } else {
            User user = new User((String) args[0], LoginCmd.hash((String) args[1]));
            return new Packet(this, null, user);
        }
    }

    public Packet execOnServer(Server server, Object object, User user) {
        try {
            server.getSqlStatements().addUser(user);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new Packet(false);
        }
        return new Packet(true);
    }
}

