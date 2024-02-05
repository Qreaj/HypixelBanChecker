package xyz.qreaj.checker;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public abstract class Packet {

    public Socket socket;

    public BufferedOutputStream bos;
    public DataOutputStream dos;

    public BufferedInputStream bis;

    public DataInputStream dis;



    public Packet(Socket socket) {
        this.socket = socket;
        try {
            this.bos = new BufferedOutputStream(socket.getOutputStream());
            this.dos = new DataOutputStream(bos);

            this.bis = new BufferedInputStream(socket.getInputStream());
            this.dis = new DataInputStream(bis);
        } catch (Exception e) {
            System.out.println("Error creating data I/O stream");
            e.printStackTrace();
        }
    }

    public void sendBuffer() {
        try {
            bos.flush(); // Flush the buffered output stream to ensure all data is sent
        } catch (Exception e) {
            System.out.println("Error flushing buffered output stream");
            e.printStackTrace();
        }
    }
    public void writeVarIntToBuffer(int input)
    {
        while ((input & -128) != 0)
        {
            this.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        this.writeByte(input);
    }

    public void writeVarLong(long value)
    {
        while ((value & -128L) != 0L)
        {
            this.writeByte((int)(value & 127L) | 128);
            value >>>= 7;
        }

        this.writeByte((int)value);
    }

    public void writeByte(int b) {
        try {
        dos.write(b);
        } catch (Exception e) {
            System.out.println("error writing byte!");
            e.printStackTrace();
        }
    }

    public void writeBytes(byte[] b) {
        try {
            dos.write(b);
        } catch (Exception e) {
            System.out.println("error writing bytes!");
            e.printStackTrace();
        }
    }


    public void writeShort(int sh) {
        try {
            dos.writeShort(sh);
        } catch (Exception e) {
            System.out.println("error writing bytes!");
            e.printStackTrace();
        }
    }

    public void writeString(String string)
    {
        byte[] abyte = string.getBytes(StandardCharsets.UTF_8);

        if (abyte.length > 32767)
        {
            System.out.println("String too big for encoder");
        }
        else
        {
            this.writeVarIntToBuffer(abyte.length);
            this.writeBytes(abyte);
        }
    }

    public String readChatComponent() throws IOException
    {
        return this.readStringFromBuffer(32767);
    }

    public byte[] readUntilEnd() {
        byte[] resultBuff = new byte[0];
        try {
            byte[] buff = new byte[1024];
            int k = -1;
            while ((k = socket.getInputStream().read(buff, 0, buff.length)) > -1) {
                byte[] tbuff = new byte[resultBuff.length + k]; // temp buffer size = bytes already read + bytes last read
                System.arraycopy(resultBuff, 0, tbuff, 0, resultBuff.length); // copy previous bytes
                System.arraycopy(buff, 0, tbuff, resultBuff.length, k);  // copy current lot
                resultBuff = tbuff; // call the temp buffer as your result buff
            }
        } catch (Exception e) {
            System.out.println("error reading byte[] until end");
            e.printStackTrace();

        }
        return resultBuff;
    }
   public byte readByte() {
        byte b = 0;
        try {
            b = dis.readByte();
        } catch (Exception e) {
            System.out.println("Error reading byte");
            e.printStackTrace();
        }
        return b;
   }

   public byte[] readBytes(byte[] buffer) {
       try {
           dis.readFully(buffer);
       } catch (Exception e) {
           System.out.println("Error reading bytes");
           e.printStackTrace();
       }
       return buffer;
   }
    public int readVarIntFromBuffer()
    {
        int i = 0;
        int j = 0;

        while (true)
        {
            byte b0 = this.readByte();
            i |= (b0 & 127) << j++ * 7;

            if (j > 5)
            {
                throw new RuntimeException("VarInt too big");
            }

            if ((b0 & 128) != 128)
            {
                break;
            }
        }

        return i;
    }

    public byte[] readByteArray()
    {
        byte[] abyte = new byte[this.readVarIntFromBuffer()];
        this.readBytes(abyte);
        return abyte;
    }

    public String readStringFromBuffer(int maxLength)
    {
        int i = this.readVarIntFromBuffer();

        if (i > maxLength * 4)
        {
            System.out.println("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")");
        }
        else if (i < 0)
        {
            System.out.println("The received encoded string buffer length is less than zero! Weird string!");
        }
        else
        {
            String s = new String(this.readBytes(new byte[i]), StandardCharsets.UTF_8);

            if (s.length() > maxLength)
            {
                System.out.println("The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
            }
            else
            {
                return s;
            }
        }
        return "";
    }

}
