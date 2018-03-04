package mine;

import crypto.*;
import core.types.*;

public class Miner {
    public Miner() {

    }

    public static void forge(Blockchain blockchain, Block... blocks){
        int height = blockchain.getHeight();
        for(int i = 0; i < blocks.length; i++){
            Block temp = blocks[i];
            blockchain.addBlock(temp, height + 1);
        }
    }
    public static void forgeRandom(Blockchain blockchain) {
        int x = (int)Math.floor(Math.random() * 19) + 1;
        int height = blockchain.getHeight();

        for(int i = 0; i < x; i++){
            blockchain.addBlock(new Block(randomString(5),randomString(20),blockchain.getLayer(height).getHashes()),height + 1);
        }
    }

    public static String randomString(int length) {
        int temp = (int) Math.floor((Math.random() * (double)Integer.MAX_VALUE));
        if (length > 32) {
            System.out.println("A string of that length cannot be generated.");
            return "";
        } else {
            String s = StringUtil.applySha256(Integer.toString(temp)).substring(0, length);
            return s;
        }
    }
}