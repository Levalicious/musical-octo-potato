package core.types.chain;

import core.types.block.Block;
import core.types.chain.layer.Finalized;
import core.types.pools.TxPool;
import core.types.transaction.Transaction;
import util.byteUtils.BIUtil;
import util.byteUtils.ByteUtil;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.rmi.activation.ActivationGroup_Stub;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import static crypto.hash.Hash.getMerkleRoot;
import static util.Hex.fromHex;
import static util.Hex.getHex;

public class Blockchain {
    public static ArrayList<Finalized> chain = new ArrayList<Finalized>();;

    public static boolean isValid() {
        for(long i = 1; i < chain.size(); i++) {
            if(!chain.get((int)i).checkBlocks()) return false;
            if(!chain.get((int)i).checkAncestors(fromHex(getMerkleRoot(chain.get((int) i - 1).getHashes())))) return false;
        }

        return true;
    }

    public static void addLayer(ArrayList<Block> layer) {
        chain.add(new Finalized(fromHex(Integer.toString(chain.size(),16)),new ArrayList<Block>(layer)));
    }

    public static void genesis() {
        Finalized genesis = new Finalized(BigInteger.ZERO.toByteArray());
        try {
            FileInputStream fin = new FileInputStream("genesis.ser");
            ObjectInputStream in = new ObjectInputStream(fin);
            genesis = (Finalized)in.readObject();
            in.close();
            fin.close();
        }catch(Exception c) {
            System.out.println("TxPool file not found.");
            c.printStackTrace();
        }

        if(chain.size() == 0) {
            chain.add(genesis);
        }else {
            chain.set(0, genesis);
        }
    }

    public static Finalized getLayer(byte[] height) {
        return chain.get(ByteUtil.byteArrayToInt(height));
    }

    public static void addBlock(Block block, int height) {
        try{
            chain.get(height).addBlock(block);
        }catch(Exception e) {
            chain.add(new Finalized(fromHex(Integer.toHexString(height))));
            chain.get(height).addBlock(block);
        }
    }

    public static void trimInval() {
        for(long i = chain.size() - 1; i > 0; i--) {
            if(!chain.get((int)i).checkBlocks()) {
                chain.remove(chain.get((int)i));
            }
            if(!chain.get((int)i).checkAncestors(fromHex(getMerkleRoot(chain.get((int) i - 1).getHashes())))) {
                chain.remove(chain.get((int)i));
            }
        }
    }

    public static Block getBlock(String hash) {
        for(Finalized layer : chain) {
            for(Block block : layer.getInstance()) {
                if(block.getBlockHash().equals(hash)) return block;
            }
        }

        return null;
    }

    public static byte[] getHeight() {
        return fromHex(Integer.toString(chain.size(), 16));
    }
}
