package xyz.qreaj.checker.packets;

import xyz.qreaj.checker.Packet;

import java.net.Socket;

public class C00PacketLoginStart extends Packet {
    private String username;

    private int id = 0x00;
    public C00PacketLoginStart(String username,Socket socket) {
        super(socket);
        this.username = username;
    }

    public void writePacket() {
        writeVarIntToBuffer(this.username.length() + String.valueOf(id).length() + 1); // + 1 because first packet takes 1 byte
        writeByte(id);
        writeString(this.username);
        sendBuffer();
    }
}
