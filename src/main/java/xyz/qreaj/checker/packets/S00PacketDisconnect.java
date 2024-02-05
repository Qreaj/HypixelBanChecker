package xyz.qreaj.checker.packets;

import xyz.qreaj.checker.Packet;
import xyz.qreaj.checker.utils.CryptManager;

import javax.crypto.SecretKey;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class S00PacketDisconnect extends Packet {

    private SecretKey secretkey;
    private boolean banned = false;
    public S00PacketDisconnect(Socket socket,SecretKey secretKey) {
        super(socket);
        this.secretkey = secretKey;
    }


    public void readPacket() {
      //  readByte();
       byte[] gay = readUntilEnd();
       byte[] decrypted = CryptManager.createNetCipherInstance(2,secretkey).update(gay);

       String decryptedMessage = new String(decrypted,StandardCharsets.UTF_8);

       //System.out.println(decryptedMessage);
       if (decryptedMessage.contains("banned")) {
           banned = true;
       }


    }

    public boolean isBanned() {
        return banned;
    }
}
