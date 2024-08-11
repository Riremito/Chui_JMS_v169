package handling.netty;

import client.MapleClient;
import constants.ServerConstants;
import handling.SendPacketOpcode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import tools.MapleAESOFB;
import java.util.concurrent.locks.Lock;
import tools.HexTool;
import tools.StringUtil;
import tools.data.ByteArrayByteStream;
import tools.data.LittleEndianAccessor;

public class MaplePacketEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf buffer) throws Exception {
        final MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
        if (client != null) {
            final MapleAESOFB send_crypto = client.getSendCrypto();
            byte[] input = ((byte[]) message);
            if (ServerConstants.ShowSendP) {
                final int pHeader = ((input[0]) & 0xFF) + (((input[1]) & 0xFF) << 8);
//                String op = SendPacketOpcode.nameOf(pHeader);
                String pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
                pHeaderStr = "0x" + StringUtil.getLeftPaddedStr(pHeaderStr, '0', 4);
                final StringBuilder sb = new StringBuilder("[發送] " + pHeaderStr);
                sb.append("\r\n\r\n").append(HexTool.toString((byte[]) message)).append("\r\n").append(HexTool.toStringFromAscii((byte[]) message));
                System.out.println(sb.toString());
//                sb.append("\r\n\r\n").append(HexTool.toString((byte[]) message)).append("\r\n").append(HexTool.toStringFromAscii((byte[]) message));
            }
            final Lock mutex = client.getLock();
            mutex.lock();
            try {
//                final byte[] header = send_crypto.getPacketHeader(input.length);
//                final byte[] input_ = send_crypto.crypt(input); // Crypt it with IV
                buffer.writeBytes(send_crypto.getPacketHeader(input.length)/*header*/);
//                buffer.writeBytes(send_crypto.getPacketHeader(((byte[]) message).length));
                buffer.writeBytes(send_crypto.crypt(input)/*input_*/);
            } finally {
                mutex.unlock();
            }
        } else { // no client object created yet, send unencrypted (hello) 這裡是發送 gethello 封包 無需加密
            buffer.writeBytes((byte[]) message);
        }
    }

/*    private String lookupRecv(int val) {
        for (SendPacketOpcode op : SendPacketOpcode.values()) {
            if (op.getValue() == val) {
                return op.name();
            }
        }
        return "UNKNOWN";
    }

    private int readFirstShort(byte[] arr) {
        return new LittleEndianAccessor(new ByteArrayByteStream(arr)).readShort();
    }*/
}
