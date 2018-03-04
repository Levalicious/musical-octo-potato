package core.types;

import resources.GenesisBlocks;

import java.util.ArrayList;
import java.util.Arrays;

public class Layer implements GenesisBlocks{
    private int height;
    private ArrayList<Block> layer;

    Layer(){
        layer = new ArrayList<Block>();
    }

    Layer(int height) {
        layer = new ArrayList<Block>();
        this.height = height;
    }

    public void setHeight(int height){
        this.height = height;
    }

    public void addBlock(Block block){
        layer.add(block);
        layer.sort((block1, block2) -> block1.getPrefix().compareTo(block2.getPrefix()));
        for(int i = 0; i < layer.size(); i++){
            layer.get(i).setDepth(i);
        }
    }

    public Block getBlock(int i){
        if((layer.size() - 1) < i){
            System.out.println("Attempted to retrieve a block that doesn't exist in this layer.");
            throw new ArrayIndexOutOfBoundsException();
        }else{
            return layer.get(i);
        }
    }

    public void genesis(){
        layer.add(GEN_ONE);
        layer.get(0).setDepth(0);
        layer.add(GEN_TWO);
        layer.get(1).setDepth(1);
        layer.add(GEN_THREE);
        layer.get(2).setDepth(2);
        layer.add(GEN_FOUR);
        layer.get(3).setDepth(3);
        height = 0;
    }

    public String toString(){
        String temp = "";
        for(int i = 0; i < layer.size(); i++){
            temp = (temp + "\n\n" + layer.get(i).toString());
        }
        return temp;
    }

    public String[] getHashes(){
        String[] temp = new String[layer.size()];
        for(int i = 0; i < layer.size(); i++){
            temp[i] = layer.get(i).calculateHash();
        }
        return temp;
    }

    public boolean checkValid(){
        String[] temp = layer.get(0).getPreviousHashes();
        for(int i = 0; i < layer.size(); i++){
            if(!(Arrays.equals(temp,layer.get(i).getPreviousHashes()))){
                return false;
            }
        }

        return true;
    }
}
