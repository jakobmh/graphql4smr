package graphql4smr.lib;

import org.junit.Test;

import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;


public class SerializableTest {

    @Test
    public void exampletest() {
        assertTrue("testing something", true);
    }

    @Test
    public void graphQL4SMRSeralizabletest() {
        GraphQL4SMR graphQL4SMR = new GraphQL4SMR();
        seralizabletestHelper(graphQL4SMR);
    }

    @Test
    public void graphQL4SMRStringWrapperSeralizabletest() {
        GraphQL4SMRStringWrapper graphQL4SMRStringWrapper = new GraphQL4SMRStringWrapper();
        seralizabletestHelper(graphQL4SMRStringWrapper);
    }
    public void seralizabletestHelper(Serializable object) {
        Serializable original = object;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(original);
            out.flush();
            bos.flush();
            out.close();
            bos.close();
            byte[] bytearray_original = bos.toByteArray();

            ByteArrayInputStream bis = new ByteArrayInputStream(bytearray_original);
            ObjectInput in = new ObjectInputStream(bis);
            Object copy = in.readObject();
            in.close();
            bis.close();

            Serializable object_copy = (Serializable)copy;


            ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
            ObjectOutput out2 = new ObjectOutputStream(bos2);
            out2.writeObject(object_copy);
            out2.flush();
            bos2.flush();
            out2.close();
            bos2.close();
            byte[] bytearray_copy = bos2.toByteArray();

            //System.out.println(bytesToHex(bytearray_original));
            //System.out.println(bytesToHex(bytearray_copy));

            assertTrue(Arrays.equals(bytearray_original, bytearray_copy));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException" +
                    " is caught");
        }

    }

    // https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
