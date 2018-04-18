package core.types.chain.layer;

import core.types.block.Block;
import core.types.transaction.Transaction;
import core.types.wrappers.WBlock;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.TreeSet;

import static resources.Config.MINER_WIF;

public class Temp {
    private static TreeSet<WBlock> tempLayer;

    public static void addBlock(byte[] height, String prefix, ArrayList<Transaction> transactions, Finalized layer) {
        WBlock block = new WBlock();

        block.setAncestors(layer.getHashes());
        block.setPayload(transactions);

        block.setHeader(height, prefix, block.getTxRoot(), block.getFees(), block.getAncestorRoot(), MINER_WIF);

        tempLayer .add(block);

    }

    public static ArrayList<byte[]> getHashes() {
        ArrayList<byte[]> hashList = new ArrayList<byte[]>();
        for(Block block : tempLayer ) {
            hashList.add(block.getBlockHash());
        }

        return hashList;
    }

    public static void evalBlocks() {
        for(WBlock block : tempLayer ) {
            block.setValid(block.checkBlock());
        }
    }

    public static void evalAncestors(String realPrevRoot) {
        for(WBlock block : tempLayer ) {
            block.setValid(block.getAncestorRoot().equals(realPrevRoot));
        }
    }

    public static void trimtempLayer () {
        for(WBlock block : tempLayer ) {
            if(!block.getValid()) tempLayer .remove(block);
        }
    }

    public static ArrayList<Block> makeFinal() {
        ArrayList<Block> temp = new ArrayList<Block>();

        for(Block block : tempLayer ) {
            temp.add(block);
        }

        return temp;
    }
}
