package resources;

import static util.Hex.fromHex;

public class Config {
    /* Max block size in bytes */
    public final static long MAX_BLOCK_SIZE = 10485760;

    /* Minimum fee per byte */

    public final static byte[] GENESIS_TX = fromHex("dec6abcd041eba418c8ccb25183dc64c80377432151c89903db5c1f1f9756b71");

    /* Miner Private Key */
    public final static byte[] MINER_WIF = fromHex("c3bcf26c8d33c10e8a1d66c66497a3cd93ea5b6554d4db760475f5e21b8d4502");
}
