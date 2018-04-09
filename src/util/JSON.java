package util;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import core.types.block.Block;
import core.types.wrappers.WBlock;

import java.io.InputStreamReader;
import java.io.Reader;

public class JSON {
    public static String toJson(Object o) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(o);
    }

    public static Block blockFromJson(String s) {
        return (new GsonBuilder().create().fromJson(s, Block.class));
    }
}
