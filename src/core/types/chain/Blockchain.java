package core.types.chain;

import core.types.block.Block;
import core.types.chain.layer.Finalized;

import java.io.UnsupportedEncodingException;
import java.rmi.activation.ActivationGroup_Stub;
import java.util.ArrayList;
import java.util.TreeSet;

import static crypto.hash.Hash.getMerkleRoot;

public class Blockchain {
    private static ArrayList<Finalized> chain;

    public Blockchain() {
        chain = new ArrayList<Finalized>();
    }

    public static boolean isValid() throws UnsupportedEncodingException {
        for(long i = 1; i < chain.size(); i++) {
            if(!chain.get((int)i).checkBlocks()) return false;
            if(!chain.get((int)i).checkAncestors(getMerkleRoot(chain.get((int) i - 1).getHashes()))) return false;
        }

        return true;
    }

    public static void addLayer(ArrayList<Block> layer) {
        chain.add(new Finalized((long)chain.size(),new TreeSet<Block>(layer)));
    }

    public static void trimInval() throws UnsupportedEncodingException{
        for(long i = chain.size() - 1; i > 0; i--) {
            if(!chain.get((int)i).checkBlocks()) {
                chain.remove(chain.get((int)i));
            }
            if(!chain.get((int)i).checkAncestors(getMerkleRoot(chain.get((int) i - 1).getHashes()))) {
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

    public static long getHeight() {
        return chain.size();
    }
}
