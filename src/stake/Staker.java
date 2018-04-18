package stake;

import core.types.block.Ancestors;
import core.types.block.Block;
import core.types.block.Header;
import core.types.block.Payload;
import core.types.chain.Blockchain;
import core.types.pools.TxPool;
import core.types.pools.UTXOPool;
import util.byteUtils.ByteUtil;

import java.math.BigInteger;
import java.util.Date;

import static resources.Config.MINER_WIF;
import static util.Hex.*;
import static crypto.hash.Hash.sha256;
import static crypto.hash.Hash.sha512;

public class Staker {
    public Staker() {

    }

    public static void stake(Blockchain blockchain, Block... blocks) {
        byte[] height = blockchain.getHeight();

        for(Block temp : blocks) {
            blockchain.addBlock(temp, ByteUtil.byteArrayToInt(ByteUtil.bigIntegerToBytes(new BigInteger(height).add(BigInteger.ONE))));
        }
    }

    public static void forgeRandom() {
        int x = (int)Math.floor(Math.random() * 19) + 1;
        byte[] height = (new BigInteger(Blockchain.getHeight()).toByteArray());
        System.out.println(getHex(height) + " : " + new Date().toString());
        Ancestors a = new Ancestors(Blockchain.getLayer(fromHex(Integer.toHexString(new BigInteger(Blockchain.getHeight()).subtract(BigInteger.ONE).intValueExact()))).getHashes());
        Payload p = new Payload(TxPool.getTxs());

        for(int i = 0; i < x; i++){
            Blockchain.addBlock(new Block(height, randomString(3), MINER_WIF, p, a), new BigInteger(height).intValueExact());
        }
    }

    public static void forge(int blockCount) {
        byte[] height = (new BigInteger(Blockchain.getHeight()).toByteArray());
        System.out.println(getHex(height) + " : " + new Date().toString());
        Ancestors a = new Ancestors(Blockchain.getLayer(fromHex(Integer.toHexString(new BigInteger(Blockchain.getHeight()).subtract(BigInteger.ONE).intValueExact()))).getHashes());
        Payload p = new Payload(TxPool.getTxs());

        for(int i = 0; i < blockCount; i++){
            Blockchain.addBlock(new Block(height, randomString(3), MINER_WIF, p, a), new BigInteger(height).intValueExact());
        }
    }

    public static String randomString(int length) {
        int temp = (int) Math.floor((Math.random() * (double)Integer.MAX_VALUE));
        if (length > 64) {
            System.out.println("A string of that length cannot be generated.");
            return "";
        } else {
            String s = sha512(Integer.toString(temp)).substring(0, length);
            return s;
        }
    }
}
