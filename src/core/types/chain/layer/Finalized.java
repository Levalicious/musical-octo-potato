package core.types.chain.layer;

import com.google.gson.GsonBuilder;
import core.types.block.Block;
import core.types.transaction.Transaction;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.TreeSet;

import static resources.Config.MINER_WIF;

public class Finalized {
    private long height;
    private TreeSet<Block> layer;

    public Finalized(long height) {
        this.height = height;
        layer = new TreeSet<Block>();
    }

    public Finalized(long height, TreeSet<Block> layer) {
        this.height = height;
        this.layer = layer;
    }

    public TreeSet<Block> getInstance() {
        return this.layer;
    }

    public void addBlock(String prefix, ArrayList<Transaction> transactions, Finalized layer) {
        Block block = new Block();

        block.setAncestors(layer.getHashes());
        block.setPayload(transactions);

        block.setHeader(height, prefix, block.getTxRoot(), block.getFees(), block.getAncestorRoot(), MINER_WIF);

        this.layer.add(block);
    }

    public ArrayList<String> getHashes() {
        ArrayList<String> hashList = new ArrayList<String>();
        for(Block block : layer) {
            hashList.add(block.getBlockHash());
        }

        return hashList;
    }

    public boolean checkBlocks() throws UnsupportedEncodingException {
        for(Block block : layer) {
            if(!block.checkBlock()) return false;
        }

        return true;
    }

    public boolean checkAncestors(String realPrevRoot) {
        String prevRoot = " ";
        boolean hasRoot = false;
        for(Block block : layer) {
            if(!hasRoot) {
                prevRoot = block.getAncestorRoot();
                hasRoot = true;
            }else {
                if(!prevRoot.equals(block.getAncestorRoot())) {
                    return false;
                }
            }
        }

        if(!prevRoot.equals(realPrevRoot)) return false;

        return true;
    }

    public void processLayer() throws UnsupportedEncodingException {
        for(Block block : layer) {
            block.processBlock();
        }
    }
}
