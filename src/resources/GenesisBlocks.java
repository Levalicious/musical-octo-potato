package resources;

import core.types.Block;

public interface GenesisBlocks {
    // Declare Hash Array for Genesis Blocks
    // SHA-256 Hash of "Sam, Kennedy, Lev, Carol, and Sami" without quotes
    String[] GEN_HASH = {"E680A1D93C66870280A8E8F72E68B4544E967A17D9D2425E7DEE0F58AEDD1EAC"};

    // Declare 4 Genesis Blocks
    Block GEN_ONE = new Block("", "Kennedy", GEN_HASH);
    Block GEN_TWO = new Block("", "Sam", GEN_HASH);
    Block GEN_THREE = new Block("", "Carol", GEN_HASH);
    Block GEN_FOUR = new Block("", "Sami", GEN_HASH);
}
