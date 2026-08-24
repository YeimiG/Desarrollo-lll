/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blockchain;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author owen7
 */
public class BlockChain {

    private List<Block> chain;
    private int complexity;
    private String proofOfWork;

    public BlockChain(int complexity, String proofOfWork) {
        this.chain = new ArrayList<>();
        this.complexity = complexity;
        this.proofOfWork = proofOfWork;
        createGenesis(); // Regla 9
    }

    // Regla 9: El bloque génesis solo guarda la marca temporal y el hash
    private void createGenesis() {
        Block genesis = new Block(0, "0000000000000000000000000000000000000000000000000000000000000000");
        this.chain.add(genesis);
        mineBlock(genesis);
    }

    public Block createBlock() {
        Block lastBlock = getLastBlock();
        Block newBlock = new Block(chain.size(), lastBlock.getHash()); // Regla 10
        this.chain.add(newBlock);
        return newBlock;
    }

    // Regla 3 y 6: Minado del último bloque con Proof of Work
    public void mineBlock(Block block) {
        String target = new String(new char[complexity]).replace('\0', proofOfWork.charAt(0));
        while (!block.getHash().substring(0, complexity).equals(target)) {
            block.setNonce(block.getNonce() + 1);
            block.setHash(block.generateHash());
        }
    }

    public Block getLastBlock() {
        return chain.get(chain.size() - 1);
    }

    public List<Block> getChain() { return chain; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Block b : chain) {
            sb.append(b.toString()).append("\n");
        }
        return sb.toString();
    }
}
