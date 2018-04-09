package util.wallet.derivation;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static crypto.hash.Hash.sha256;
import static util.Hex.fromHex;
import static util.Hex.getHex;

public class MnemonicCode {
    static WordList wordList = English.INSTANCE;

    public static String createMnemonic(final byte[] entropy) {
        final int ent = entropy.length * 8;
        if (ent < 128) throw new RuntimeException("Entropy too low, 128-256 bits allowed");
        if (ent > 256) throw new RuntimeException("Entropy too high, 128-256 bits allowed");
        if (ent % 32 > 0) throw new RuntimeException("Number of entropy bits must be divisible by 32");

        final int cs = ent / 32;

        final int ms = (ent + cs) / 11;

        final byte[] entropyWithChksum = Arrays.copyOf(entropy, entropy.length + 1);

        final byte[] hash = fromHex(sha256(getHex(entropy)));

        entropyWithChksum[entropy.length] = hash[0];

        final StringBuilder sb = new StringBuilder();

        for(int i = 0; i < ent + cs; i += 11) {
            final int wordIndex = next11Bits(entropyWithChksum, i);
            sb.append(wordList.getWord(wordIndex));
            sb.append(" ");
        }

        sb.setLength(sb.length() - 1);

        return sb.toString();
    }

    static int next11Bits(byte[] bytes, int offset) {
        final int skip = offset / 8;
        final int lowerBitsToRemove = (3 * 8 - 11) - (offset % 8);
        return (((int) bytes[skip] & 0xff) << 16 |
                ((int) bytes[skip + 1] & 0xff) << 8 |
                (lowerBitsToRemove < 8
                        ? ((int) bytes[skip + 2] & 0xff)
                        : 0)) >> lowerBitsToRemove & (1 << 11) - 1;
    }

    public static byte[] getSeed(String mnemonic, String passphrase) throws Exception {
        mnemonic = Normalizer.normalize(mnemonic, Normalizer.Form.NFKD);
        passphrase = Normalizer.normalize(passphrase, Normalizer.Form.NFKD);

        byte[] salt = ("mnemonic" + passphrase).getBytes("UTF-8");

        char[] chars = mnemonic.toCharArray();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, 2048, 512);
        SecretKeyFactory fact = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        return fact.generateSecret(spec).getEncoded();
    }

    private boolean validate(String mnemonic) {
        //build a map to look up word indexes
        Map<String, Integer> map = new HashMap<>(1 << 11);
        for (int i = 0; i < 1 << 11; i++) {
            map.put(wordList.getWord(i), i);
        }

        //split the mnemonic
        String[] words = mnemonic.split(String.valueOf(wordList.getSpace()));

        //reverse calculate some of the variables from mnemonic generation, ms, ent, cs
        final int ms = words.length;

        final int entPlusCs = ms * 11;
        final int ent = (entPlusCs * 32) / 33;
        final int cs = ent / 32;
        if (entPlusCs != ent + cs)
            throw new RuntimeException("Not a correct number of words");
        byte[] entropyWithChecksum = new byte[(entPlusCs + 7) / 8];

        //look up the words
        int[] wordIndexes = new int[ms];
        for (int i = 0; i < ms; i++) {
            String word = words[i];
            Integer index = map.get(word);
            if (index == null) throw new RuntimeException("Word not found in word list \"" + word + "\"");
            wordIndexes[i] = index;
        }

        //build
        for (int i = 0, bi = 0; i < ms; i++, bi += 11) {
            writeNext11(entropyWithChecksum, wordIndexes[i], bi);
        }

        //strip the last byte
        byte[] entropy = Arrays.copyOf(entropyWithChecksum, entropyWithChecksum.length - 1);
        byte lastByte = entropyWithChecksum[entropyWithChecksum.length - 1];

        //recalculate hash
        byte[] shaTemp = fromHex(sha256(getHex(entropy)));
        byte sha = shaTemp[0];

        //we only want to compare the first cs bits
        byte mask = (byte) ~((1 << (8 - cs)) - 1);

        //if the first cs bits are the same, it's valid
        return ((sha ^ lastByte) & mask) == 0;
    }

    private void writeNext11(byte[] bytes, int value, int offset) {
        int skip = offset / 8;
        int bitSkip = offset % 8;
        {//byte 0
            byte firstValue = bytes[skip];
            byte toWrite = (byte) (value >> (3 + bitSkip));
            bytes[skip] = (byte) (firstValue | toWrite);
        }

        {//byte 1
            byte valueInByte = bytes[skip + 1];
            final int i = 5 - bitSkip;
            byte toWrite = (byte) (i > 0 ? (value << i) : (value >> -i));
            bytes[skip + 1] = (byte) (valueInByte | toWrite);
        }

        if (bitSkip >= 6) {//byte 2
            byte valueInByte = bytes[skip + 2];
            byte toWrite = (byte) (value << 13 - bitSkip);
            bytes[skip + 2] = (byte) (valueInByte | toWrite);
        }
    }
}
