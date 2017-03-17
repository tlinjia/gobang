package main.Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by lin on 2017/3/17/0017.
 * 格式化类
 */
public class Formatter {
    /**
     * 整数 --> 4字节小端结尾
     * @param n
     * @return
     */
    public static byte[] toBytesLE(int n){
        byte [] b =  new   byte [ 4 ];
        b[0 ] = ( byte ) (n &  0xff );
        b[1 ] = ( byte ) (n >>  8  &  0xff );
        b[2 ] = ( byte ) (n >>  16  &  0xff );
        b[3 ] = ( byte ) (n >>  24  &  0xff );
        return  b;
    }

    public static byte[] constructPackage(String msg){
        byte[] length = Formatter.toBytesLE(msg.length());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.write(length);
            dataOutputStream.writeBytes(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }
}
