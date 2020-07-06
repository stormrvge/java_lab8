package sample.commands;

import sample.connection.client.Client;
import sample.logic.Packet;
import sample.logic.User;
import sample.connection.server.Server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Formatter;
import java.util.FormatterClosedException;

public class LoginCmd extends Command {

    public LoginCmd() {
        super(false);
    }

    public boolean getRequireLogin() {
        return require_login;
    }

    @Override
    public Packet execOnClient(Client client, String ... args) {
        if (args.length != 2) {
            System.err.println("Incorrect number of arguments!");
        } else {
            User user = new User(args[0], hash(args[1]));
            client.setUser(user);
            return new Packet(this, null, user);
        }
        return null;
    }

    public Packet execOnServer(Server server, Object object, User user) {
        try {
            if (server.getSqlStatements().login(user)) {
                return new Packet(true);
            } else {
                return new Packet(false);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new Packet(false);
        }
    }

    public static String hash(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(str.getBytes(StandardCharsets.UTF_8));
            return byteToHex(digest.digest());
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        try {
            formatter.close();
        } catch (FormatterClosedException e) {
            System.err.println(e.getMessage());
        }

        return result;
    }
}
