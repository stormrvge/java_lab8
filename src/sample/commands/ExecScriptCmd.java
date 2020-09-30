package sample.commands;

import sample.connection.client.Client;
import sample.connection.server.Server;
import sample.io.FileHandler;
import sample.logic.Invoker;
import sample.logic.Packet;
import sample.logic.User;

import java.io.IOException;
import java.util.ArrayList;

public class ExecScriptCmd extends Command {
    private final Client client;
    private final Invoker invoker;
    private final ArrayList<String> file_already_run;
    private final ArrayList<Packet> packetArrayList;
    public static boolean is_ExecCmd = false;

    private static final int max_recursion_depth = 100;
    private int current_depth;

    public ExecScriptCmd(Client client) {
        super("Execute Script", true, false);
        this.client = client;
        this.file_already_run = new ArrayList<>();
        this.current_depth = 0;
        this.packetArrayList = new ArrayList<>();

        invoker = new Invoker();
        invoker.register("help", new HelpCmd());
        invoker.register("info", new InfoCmd());
        invoker.register("print_unique_distance", new UniqueDistanceCmd());
        invoker.register("show", new ShowCmd());
    }

    public ArrayList<Packet> execute(String ... cmd_args) {
        is_ExecCmd = true;
        if (client.getUser().getLoginState()) {
            ++current_depth;
            final int num_args = 1;


            if ((cmd_args.length) != num_args) {
                System.err.println("Execute script cannot take " + cmd_args.length + " arguments.");
            } else if (max_recursion_depth <= current_depth) {
                System.err.println("Maximum recursion depth reached!");
                System.err.println("Ignore execute_script commands!");
            } else {
                try {
                    String cmd;
                    FileHandler file = new FileHandler(cmd_args[0], FileHandler.READ);
                    boolean is_founded = false;


                    for (String file_run : file_already_run) {
                        if (file_run.equals(cmd_args[0])) {
                            System.err.println("Recursion detected!");
                            System.err.println("Current recursion depth: " + ++this.current_depth);
                            System.err.println("Max recursion depth: " + max_recursion_depth);
                            is_founded = true;
                            break;
                        }
                    }

                    if (!is_founded) {
                        file_already_run.add(cmd_args[0]);
                    }

                    while ((cmd = file.readline()) != null) {
                        cmd = cmd.trim().replace('\t', ' ');
                        while (cmd.contains("  ")) {
                            cmd = cmd.replace("  ", " ");
                        }
                        Command command = invoker.createCommand(cmd);
                        String[] args = invoker.getArgs();
                        if (!invoker.getCommandName().equals("execute_script")) {
                            Packet packet = command.execOnClient(client, args);
                            packetArrayList.add(packet);
                        }
                        else if (invoker.getCommandName().equals("execute_script")) {
                            execute(args);
                        }
                    }
                    file.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    is_ExecCmd = false;
                    return null;
                }
            }
            is_ExecCmd = false;
            return packetArrayList;

        } else {
            is_ExecCmd = false;
            System.err.println("You must login!");
        }
        is_ExecCmd = false;
        return null;
    }

    @Override
    public Packet execOnServer(Server server, Object objects, User user) {
        return null;
    }

    @Override
    public Packet execOnClient(Client client, Object ... args) {
        return null;
    }

    public boolean getRequireLogin() {
        return require_login;
    }
}
