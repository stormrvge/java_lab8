package sample.connection.server.threads;

import sample.connection.server.Server;
import sample.logic.Packet;

import java.util.concurrent.Callable;

public class Handler implements Callable<Packet> {
    private final Packet packet;
    private final Server server;

    Handler(Packet packet, Server server) {
        this.packet = packet;
        this.server = server;
    }

    @Override
    public Packet call() {
        return packet.getCommand().execOnServer(server, packet.getArgument(), packet.getUser());
    }
}
