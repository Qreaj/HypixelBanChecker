package xyz.qreaj.checker.packets;

import xyz.qreaj.checker.Packet;

import java.net.Socket;

public class C00Handshake extends Packet {

    private int protocolVersion;
    private String ip;
    private int port;
    private int requestedState;

    private int id = 0x00;

    public C00Handshake(int version, String ip, int port, Socket socket)
    {
        super(socket);
        this.protocolVersion = version;
        this.ip = ip;
        this.port = port;
        this.requestedState = 2; // 2 == "LOGIN" ENUM
    }

    public void writePacket() {

        writeVarIntToBuffer(this.ip.length() + String.valueOf(port).length() + String.valueOf(this.requestedState).length()); // PACKET LENGTH
        writeByte(id); // PACKET ID
        writeVarIntToBuffer(this.protocolVersion); // PROTOCOL VERSION
        // PACKET DATA byte[]
        writeString(this.ip);
        writeShort(this.port);
        writeVarIntToBuffer(this.requestedState);
        sendBuffer();
    }
}
