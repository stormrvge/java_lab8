package sample.commands;

import sample.connection.client.Client;
import sample.connection.server.Server;
import sample.logic.CollectionManager;
import sample.logic.Packet;
import sample.logic.User;

import java.io.Serializable;

public abstract class Command implements Serializable {
    private String name;
    final boolean require_login;

    protected Command(String name, boolean require_login) {
        this.name = name;
        this.require_login = require_login;
    }

    abstract public boolean getRequireLogin();
    abstract public Packet execOnServer(Server server, Object args, User user);
    abstract public Packet execOnClient(Client client, Object ... args);
    public void serverCmd(CollectionManager collectionManager) {}

    public String getName() {
        return name;
    }
}
