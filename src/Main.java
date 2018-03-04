import java.io.IOException;
import core.types.*;
import mine.*;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException{
        Blockchain blockchain = new Blockchain();

        Miner forger = new Miner();

        long start = System.currentTimeMillis();
        System.out.println();
        System.out.println();
        for(int i = 0; i < 200; i++){
            forger.forgeRandom(blockchain);
        }

        if(blockchain.checkValid()){
            System.out.println("Blockchain is valid.");
        }else{
            System.out.println("Blockchain is invalid.");
        }

        long end = System.currentTimeMillis();
        Files.write(Paths.get("out.txt"), blockchain.toJson().getBytes());
        System.out.println("Blockchain written to out.txt.");
        System.out.println("Took " + (end - start) + "ms");
    }
}
