package xyz.qreaj.checker;

import xyz.qreaj.checker.packets.C00Handshake;
import xyz.qreaj.checker.packets.C00PacketLoginStart;
import xyz.qreaj.checker.packets.C01PacketEncryptionResponse;
import xyz.qreaj.checker.packets.S00PacketDisconnect;
import xyz.qreaj.checker.utils.Session;

import java.net.Socket;

public class Main {


    final static String ip = "mc.hypixel.net";
    final static  int port = 25565;

    final static int protoclVersion = 107;
    public static void main(String args[]) {

            try {
                Session session = new Session("ProToTypMMM","5fa6a54012974d6e98f529918258d34b","eyJraWQiOiJhYzg0YSIsImFsZyI6IkhTMjU2In0.eyJ4dWlkIjoiMjUzNTQzODk5MDk5NzUwMiIsImFnZyI6IkFkdWx0Iiwic3ViIjoiZDViZWIwZDItOWNlNy00OWMwLTlhY2YtOWVhMjQ3ZGFkZTRkIiwiYXV0aCI6IlhCT1giLCJucyI6ImRlZmF1bHQiLCJyb2xlcyI6W10sImlzcyI6ImF1dGhlbnRpY2F0aW9uIiwiZmxhZ3MiOlsidHdvZmFjdG9yYXV0aCIsIm1zYW1pZ3JhdGlvbl9zdGFnZTQiLCJvcmRlcnNfMjAyMiIsIm11bHRpcGxheWVyIl0sInByb2ZpbGVzIjp7Im1jIjoiNWZhNmE1NDAtMTI5Ny00ZDZlLTk4ZjUtMjk5MTgyNThkMzRiIn0sInBsYXRmb3JtIjoiVU5LTk9XTiIsInl1aWQiOiI0NGM0NzhmZjc0ZjQ0MDY4OGZmN2Q0NjE5YTY5NzU2ZCIsIm5iZiI6MTcwNzEzNDk4MSwiZXhwIjoxNzA3MjIxMzgxLCJpYXQiOjE3MDcxMzQ5ODF9.W-WHMppwyp6fHI9bYcEN0BiKD4mIa83Qzy7YDdae7MI","MICROSOFT");
                Socket socket = new Socket(ip, port);

                C00Handshake handshake = new C00Handshake(protoclVersion, ip, port, socket);
                handshake.writePacket();

                C00PacketLoginStart packetLoginStart = new C00PacketLoginStart(session.getUsername(), socket);
                packetLoginStart.writePacket();

                C01PacketEncryptionResponse c01PacketEncryptionResponse = new C01PacketEncryptionResponse(socket,session);
                c01PacketEncryptionResponse.readANDwritePacket();

                S00PacketDisconnect s00PacketDisconnect = new S00PacketDisconnect(socket,c01PacketEncryptionResponse.getSecretkey());
                s00PacketDisconnect.readPacket();

                if (s00PacketDisconnect.isBanned()) {
                    System.out.println("Banned");
                    // kod na banned playera
                } else {
                    System.out.println("Unbanned");
                    // kod na unbanned playera
                }
            } catch (Exception e) {
                System.out.println("Error creating socket or connection closed");
                e.printStackTrace();
            }

        }

}
