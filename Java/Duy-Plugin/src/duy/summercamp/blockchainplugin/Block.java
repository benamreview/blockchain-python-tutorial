package duy.summercamp.blockchainplugin;

public class Block {
    public int block_number;
    public int nonce;
    public String previous_hash;
    public long timestamp;
    public Transaction[] transactions;
}
