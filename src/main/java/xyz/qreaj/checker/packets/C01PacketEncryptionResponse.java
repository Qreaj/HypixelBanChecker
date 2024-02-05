package xyz.qreaj.checker.packets;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import xyz.qreaj.checker.Packet;
import xyz.qreaj.checker.utils.CryptManager;
import xyz.qreaj.checker.utils.Session;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.Proxy;
import java.net.Socket;
import java.security.PublicKey;
import java.util.UUID;

public class C01PacketEncryptionResponse extends Packet {

    private int id = 0x01;

    private PublicKey publicKey;

    private SecretKey secretkey;
    private byte[] verifyToken;

    private String hashedServerId;

    private byte[] secretKeyEncrypted = new byte[0];
    private byte[] verifyTokenEncrypted = new byte[0];
    private Session session;
    public C01PacketEncryptionResponse(Socket socket,Session session) {
        super(socket);
        this.secretkey = CryptManager.createNewSharedKey();
        this.session = session;
    }

    public void readANDwritePacket() {
        int len = readVarIntFromBuffer(); // read packet length
        readVarIntFromBuffer(); // read packet id




        this.hashedServerId = readStringFromBuffer(20);
        this.publicKey = CryptManager.decodePublicKey(readByteArray());
        this.verifyToken = readByteArray();

        String s1 = (new BigInteger(CryptManager.getServerIdHash(hashedServerId, publicKey, secretkey))).toString(16);
      //  System.out.println(s1);
        try {
            YggdrasilMinecraftSessionService sessionService = (YggdrasilMinecraftSessionService) (new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString())).createMinecraftSessionService();
            sessionService.joinServer(session.getProfile(), session.getToken(), s1);
        } catch (Exception e) {
            System.out.println("Error sending request to minecraft api");
            e.printStackTrace();
        }

        this.secretKeyEncrypted = CryptManager.encryptData(publicKey, secretkey.getEncoded());
        this.verifyTokenEncrypted = CryptManager.encryptData(publicKey, verifyToken);

        writeVarIntToBuffer(this.secretKeyEncrypted.length + this.verifyTokenEncrypted.length + 5);
        writeByte(id);

        writeVarIntToBuffer(this.secretKeyEncrypted.length);
        writeBytes(this.secretKeyEncrypted);
        writeVarIntToBuffer(this.verifyTokenEncrypted.length);
        writeBytes(this.verifyTokenEncrypted);

        sendBuffer();


    }

    public SecretKey getSecretkey() {
        return secretkey;
    }
}
