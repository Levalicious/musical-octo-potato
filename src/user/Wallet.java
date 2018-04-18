package user;

import util.wallet.ECKey;

import static util.Hex.fromHex;
import static util.Hex.getHex;

public class Wallet {
    ECKey keyPair;


    public Wallet() {
        keyPair = new ECKey();
    }

    public Wallet(String privateKey) {
        keyPair = ECKey.fromPrivate(fromHex(privateKey));
    }

    public String toString() {
        if(keyPair.hasPrivKey()) {
            return keyPair.toStringWithPrivate();
        }else {
            return keyPair.toString();
        }
    }

    public String getPubAddress() {
        return "0x" + getHex(keyPair.getAddress());
    }

    public byte[] getPubKey() {
        return keyPair.getPubKey();
    }

    public byte[] getPrivKey() {
        return keyPair.getPrivKeyBytes();
    }

    public String getPubKeyString() {
        return "0x" + getHex(keyPair.getPubKey());
    }

    public String getPrivKeyString() {
        return "0x" + getHex(keyPair.getPrivKeyBytes());
    }
}
