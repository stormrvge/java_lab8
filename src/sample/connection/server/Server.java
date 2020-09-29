package sample.connection.server;

import sample.commands.*;
import sample.connection.server.threads.Reader;
import sample.logic.CollectionManager;
import sample.logic.Invoker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Server {
    private final int port;

    private ArrayList<Socket> socketArrayList;
    private HashMap<Socket, Reader> readerHashMap;

    private ServerSocket server;
    private CollectionManager manager;
    private Connection database;
    private SQLStatements sqlStatements;

    private final File connectionProperties;
    private final Properties properties;

    private int numOfClients;


    public Server (int port) {
        this.port = port;
        properties = new Properties();
        connectionProperties = new File("connection.properties");
    }

    public void process() {
        try {
            server = new ServerSocket(port);
            server.setSoTimeout(1000);
            System.out.println("Server started on: " + server.getInetAddress());


            properties.load(new FileInputStream(connectionProperties));
            String url = properties.getProperty("url");
            String user = properties.getProperty("login");
            String password = properties.getProperty("password");

            database = DriverManager.getConnection(url, user, password);

            readerHashMap = new HashMap<>(20);
            socketArrayList = new ArrayList<>(20);
            Invoker invoker = new Invoker();
            manager = new CollectionManager();
            sqlStatements = new SQLStatements(database, manager);
            //sqlStatements.load();

            BufferedReader inputCmd = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Type \"help\" for list of available commands.");
            while (!server.isClosed()) {
                if (!inputCmd.ready()) {
                    try {
                        Socket clientSocket = server.accept();
                        if (clientSocket != null) {
                            socketArrayList.add(clientSocket);
                            numOfClients++;

                            Reader reader = new Reader(this, clientSocket);
                            readerHashMap.put(clientSocket, reader);
                            reader.start();
                        }
                    } catch (SocketTimeoutException ignored) {}
                }
                else if (inputCmd.ready()) {
                    Command command = invoker.createCommand(inputCmd.readLine().trim());
                    try {
                        command.serverCmd();
                    }  catch (NullPointerException ignored) {}
                }
            }
        } catch (IOException | SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            System.out.println("Server closed.");
        }
    }

    public void sync() throws ExecutionException, InterruptedException, IOException {
        for(Map.Entry<Socket, Reader> entry : readerHashMap.entrySet()) {
            Reader reader = entry.getValue();
            reader.sync();
        }
    }

    public void closeConnection(Socket clientSocket) {
        try {
            clientSocket.close();
            readerHashMap.remove(clientSocket);
            socketArrayList.remove(clientSocket);
            --numOfClients;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void stopServer() {
        try {
            if (!socketArrayList.isEmpty()) {
                for (Socket sockets : socketArrayList) {
                    sockets.close();
                }
            }
            server.close();
            System.out.println("Server was stopped!");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (NullPointerException e) {
            System.err.println();
        }
    }

    public CollectionManager getManager() {
        return manager;
    }

    public Connection getDataBase() {
        return database;
    }

    public SQLStatements getSqlStatements() {return sqlStatements;}

    public int getNumOfClients() {
        return numOfClients;
    }
}