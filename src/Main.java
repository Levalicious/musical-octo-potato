import com.google.gson.GsonBuilder;
import core.types.block.Ancestors;
import core.types.block.Block;
import core.types.block.Payload;
import core.types.chain.Blockchain;
import core.types.chain.layer.Finalized;
import core.types.pools.TxPool;
import core.types.transaction.Transaction;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import stake.Staker;
import util.wallet.ECKey;

import java.io.*;
import java.math.BigInteger;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import static resources.Config.MINER_WIF;
import static util.Hex.fromHex;
import static util.Hex.getHex;

public class Main {
    public static void main(String[] args) {
        /*
        ECKey keypair = ECKey.fromPrivate(MINER_WIF);
        System.out.println(getHex(keypair.getPrivKeyBytes()));
        System.out.println(getHex(keypair.getAddress()));
        ECKey.ECDSASignature sig = keypair.sign(crypto.hash.Hash.byteHash.blake256("Hello".getBytes()));
        byte[] signedData = crypto.hash.Hash.byteHash.blake256("Hello".getBytes());
        try{
            if(!Arrays.equals(ECKey.computeAddress(ECKey.signatureToKeyBytes(signedData, sig)), keypair.getAddress())) System.out.println(false);
            System.out.println(ECKey.verify(signedData, sig, ECKey.signatureToKeyBytes(signedData, sig)));
        }catch(SignatureException s) {
            System.out.println("Failed.");
        }

        byte[] g = null;

        if(g == null) {
            System.out.println("null");
        }

        System.out.println("Here comes the blockchain Shit.");
        // Transaction tx = new Transaction(true, fromHex("0000000000000000000000000000000000000000000000000000000000000000"), fromHex("aa71ffac179f40396ee0a22cee23e95933e0ba93a0c998b109ab951870335c93"), new BigInteger("100000000000000000000000000000000000").toByteArray(), "I have a crush on Samuel Ronald Evans.".getBytes("UTF-8"), null, MINER_WIF);
        Transaction tx = new Transaction();
        try {
            FileInputStream fin = new FileInputStream("genesisTx.ser");
            ObjectInputStream in = new ObjectInputStream(fin);
            tx = ((Transaction) in.readObject());
            in.close();
            fin.close();
        }catch(Exception c) {
            System.out.println("TxPool file not found.");
            c.printStackTrace();
        }

        System.out.println(getHex(tx.calcHashWithData()));

        ArrayList<Transaction> txList = new ArrayList<>();

        txList.add(tx);

        Finalized genesis = new Finalized(BigInteger.ZERO.toByteArray());
        ArrayList<byte[]> ancestors = new ArrayList<>();
        ancestors.add(fromHex(crypto.hash.Hash.blake256("Sam, Kennedy, Sami, Carol")));
        Payload p = new Payload(txList);
        Ancestors a = new Ancestors(ancestors);
        Block block = new Block(BigInteger.ZERO.toByteArray(), "0", MINER_WIF, p, a);
        genesis.addBlock(block);

        System.out.println(getHex(block.getBlockHash()));

        try {
            FileOutputStream fout = new FileOutputStream(("genesis.ser"));
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(genesis);
            out.close();
            fout.close();
            System.out.println("TxPool saved!");
        }catch(IOException i) {
            i.printStackTrace();
        }
        */

        Blockchain.genesis();
        System.out.println("00 : " + new Date().toString());
        for(int i = 0; i < 239; i++) {
            /*
            try {
                Thread.sleep(15000);
            }catch(InterruptedException e) {
                System.out.println("Wait time between blocks failed.");
            }
            */
            Staker.forgeRandom();
        }

        try {
            Thread.sleep(15000);
        }catch(InterruptedException e) {
            System.out.println("Wait time between blocks failed.");
        }

        Staker.forge(1);

        System.out.println(Blockchain.isValid());
        System.out.println(new GsonBuilder().create().toJson(Blockchain.chain));
    }
}
