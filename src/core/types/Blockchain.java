package core.types;

import java.util.ArrayList;
import java.util.Arrays;
import com.google.gson.GsonBuilder;

import static resources.GenesisBlocks.GEN_HASH;

public class Blockchain {
    private ArrayList<Layer> blockchain;

    public Blockchain(){
        blockchain = new ArrayList<Layer>();
        genesis();
    }

    public void addBlock(Block block, int height){
        if(height > (blockchain.size() - 1)){
            if(height != blockchain.size()){
                System.out.println("Error: Cannot add layer " + height + " without the previous " + (height - (blockchain.size() - 1)) + " layers.");
                return;
            }else{
                blockchain.add(new Layer(height));
            }
        }
        blockchain.get(height).addBlock(block);
    }

    public void genesis(){
        blockchain.add(new Layer());
        blockchain.get(0).genesis();
    }

    public Layer getLayer(int height){
        return blockchain.get(height);
    }

    public int getHeight(){
        return blockchain.size() - 1;
    }

    public boolean checkValid(){
        if(!Arrays.equals(blockchain.get(0).getBlock(0).getPreviousHashes(),GEN_HASH)){
            if(!blockchain.get(0).checkValid()){
                System.out.println("Genesis blocks have inconsistent hash arrays.");
                return false;
            }
            System.out.println("Previous hash of genesis blocks does not match config.");
            return false;
        }

        for(int i = 1; i < blockchain.size(); i++){
            if(!blockchain.get(i).checkValid()) {
                System.out.println("Layer " + i + " has inconsistent hash arrays.");
                return false;
            }

            if(!Arrays.equals(blockchain.get(i).getBlock(0).getPreviousHashes(), blockchain.get(i - 1).getHashes())){
                System.out.println("The hash arrays in layer " + i + " do not match the hashes of the previous layer.");
                return false;
            }
        }

        return true;
    }

    public String toJson(){
        String temp = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        return temp;
    }
}
