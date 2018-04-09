package core.types.wrappers;

import core.types.block.Block;

public class WBlock extends Block {
    private boolean isValid = true;

    public void setValid(boolean in) {
        this.isValid = in;
    }

    public boolean getValid() {
        return isValid;
    }
}
