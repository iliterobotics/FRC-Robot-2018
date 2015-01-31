package org.ilite.vision.camera.axis;

public class Base64Encoder {
    public String encode(String s) {
        while (s.length() % 3 != 0)
            s += (char) 0;
        String encoded = "";
        for (int i = 0; i < s.length(); i += 3) {
            int src = ((int) s.charAt(i) << 16) + ((int) s.charAt(i + 1) << 8)
                    + (int) s.charAt(i + 2);
            encoded += encodeChar((src & 0xFC0000) >> 18);
            encoded += encodeChar((src & 0x03F000) >> 12);
            encoded += encodeChar((src & 0x000FC0) >> 6);
            encoded += encodeChar(src & 0x00003F);
        }
        encoded = encoded.replaceAll("AA$", "==");
        encoded = encoded.replaceAll("A$", "=");
        return encoded;
    }

    private char encodeChar(int c) {
        if (c == 63)
            return (char) 47;
        if (c == 62)
            return (char) 43;
        if (c >= 52)
            return (char) (c - 4);
        if (c >= 26)
            return (char) (c + 71);
        return (char) (c + 65);
    }
}