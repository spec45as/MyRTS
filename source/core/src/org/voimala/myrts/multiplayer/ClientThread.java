package org.voimala.myrts.multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import org.voimala.myrts.scenes.gameplay.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ClientThread extends Thread {

    private static final String TAG = ClientThread.class.getName();
    private SocketHints socketHints;
    private int port = 0;
    private String ip;
    private Socket socket;
    private boolean running = true;
    private Player player;
    private SocketType socketType;

    /** Used when the server creates a new thread for the connected client. */
    public ClientThread(final Socket socket) {
        super(ClientThread.class.getName());

        socketHints = new SocketHints();
        socketHints.connectTimeout = 10000;
        this.socket = socket;
        this.socketType = SocketType.PLAYER_SOCKET;
        player = new Player();
    }

    /** Used for connecting to the server. */
    public ClientThread(final String ip, final int port) {
        super(ClientThread.class.getName());

        socketHints = new SocketHints();
        socketHints.connectTimeout = 10000;
        this.ip = ip;
        this.port = port;
        this.socketType = SocketType.SERVER_SOCKET;
        player = new Player();
    }

    public void run() {
        connectToTheServer();

        while (running) {
            if (socketType == SocketType.SERVER_SOCKET) {
                Gdx.app.debug(TAG, "Listening messages from the server.");
            } else if (socketType == SocketType.PLAYER_SOCKET) {
                Gdx.app.debug(TAG, "Listening messages from the player.");
            }



            try {
                InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());
                char[] readCharacter = new char[1];
                String message = "";
                while (true) {
                    inputStream.read(readCharacter);
                    message += readCharacter[0];

                    // End of message reached
                    if (readCharacter[0] == '>') {
                        break;
                    }
                }

                if (socketType == SocketType.SERVER_SOCKET) {
                    Gdx.app.debug(TAG, "Got message from the server: " + message); // TODO Dies for some reason
                } else if (socketType == SocketType.PLAYER_SOCKET) {
                    Gdx.app.debug(TAG, "Got message from the player: " + message);
                }

                RTSProtocolManager.getInstance().handleNetworkMessage(message, socketType);
            } catch (Exception e) {
                Gdx.app.debug(TAG, "ERROR: while reading buffer: " + e.getMessage());
                running = false;
            }
        }

        Gdx.app.debug(TAG, "Socket disconnected.");
    }

    private void connectToTheServer() {
        if (socket == null) {
            Gdx.app.debug(TAG, "Connecting to the server...");
            socket = Gdx.net.newClientSocket(Net.Protocol.TCP, ip, port, socketHints);

            Gdx.app.debug(TAG, "Connected to the server.");
        }
    }

    public void sendMessage(final String message) {
        try {
            Gdx.app.debug(TAG, "Sending message to the server: " + message);
            socket.getOutputStream().write(message.getBytes());
        } catch (IOException e) {
            if (socketType == SocketType.SERVER_SOCKET) {
                Gdx.app.debug(TAG, "WARNING: Unable to send message to server: " + e.getMessage());
            } else if (socketType == SocketType.PLAYER_SOCKET) {
                Gdx.app.debug(TAG, "WARNING: Unable to send message to player: " + e.getMessage());
            }
        }
    }

    public void die() {
        running = false;
    }

    public Socket getSocket() {
        return socket;
    }
}