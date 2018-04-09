package resources;

import crypto.hash.Keccak;

import java.math.BigInteger;
import java.util.Scanner;

public class Config {
    /* Initialize one Keccak instance for all sponge hashes */
    public final static Keccak keccak = new Keccak();

    /* Largest allowed block size in bytes */
    public final static long MAX_BLOCK_SIZE = 10;


    /* Min fee per byte */
    public final static BigInteger MIN_BYTE_FEE = new BigInteger("1");

    /* Curve for keypairs */
    public final static String curve = "secp256k1";

    /* Blake256 hash of genesis transaction */
    public final static String GENESIS_TX = "";

    /* Miner WIF */
    public static String MINER_WIF = "";

    /* Multiset Hash Constants */
    BigInteger c1 = new BigInteger("36244126860220137202309515173162163415911310919164551451252322087109193164133115104");
    BigInteger c2 = new BigInteger("283122816218612297147754244362670172452402415711048169191135143462203017318969129");
    BigInteger c3 = new BigInteger("77919251451997649238342061511206176151081051142161854411627912237964134255230");
    BigInteger c4 = new BigInteger("237620272139911655618217414381158109242502481861781191619814293139156985717318296");

    public static void getAccount() {
        Scanner s = new Scanner("config.txt");
        String temp = s.next();
        if(temp.startsWith("WIF=")) {
            temp = temp.substring(4);
        } else {
            System.out.println("Error: Config should contain \"WIF=<Account WIF>\"");
            System.exit(0);
        }

        MINER_WIF = temp;
    }
}
