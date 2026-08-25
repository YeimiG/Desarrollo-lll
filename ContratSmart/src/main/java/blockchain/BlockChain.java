package blockchain;

import java.util.ArrayList;
import java.util.List;

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

    // Regla 9: El bloque génesis solo guarda la marca temporal y el hash base
    private void createGenesis() {
        Block genesis = new Block(0, "0000000000000000000000000000000000000000000000000000000000000000");
        this.chain.add(genesis);
        mineBlock(genesis);
    }

    // Regla 10: Crea un nuevo bloque enlazado al último hash registrado
    public Block createBlock() {
        Block lastBlock = getLastBlock();
        Block newBlock = new Block(chain.size(), lastBlock.getHash());
        this.chain.add(newBlock);
        return newBlock;
    }

    // Regla 3 y 6: Minado del bloque mediante prueba de trabajo (Proof of Work)
    public void mineBlock(Block block) {
        String target = new String(new char[complexity]).replace('\0', proofOfWork.charAt(0));
        while (!block.getHash().substring(0, complexity).equals(target)) {
            block.setNonce(block.getNonce() + 1);
            block.setHash(block.generateHash());
        }
    }

    // Valida un bloque externo (ej. recibido de otra sucursal) antes de añadirlo
    public boolean addProvedBlock(Block block) {
        String target = new String(new char[complexity]).replace('\0', proofOfWork.charAt(0));
        
        // Verifica que el bloque apunte al hash correcto y cumpla la dificultad
        if (block.getPreviousHash().equals(getLastBlock().getHash()) && 
            block.getHash().substring(0, complexity).equals(target) &&
            block.getHash().equals(block.generateHash())) {
            
            this.chain.add(block);
            return true;
        }
        return false;
    }

    // Reporte de contratos de un bloque en específico
    public String getContractReport(int blockIndex) {
        if (blockIndex < 0 || blockIndex >= chain.size()) return "Bloque no encontrado.";
        
        StringBuilder sb = new StringBuilder();
        Block blk = chain.get(blockIndex);
        
        sb.append("--- REPORTE DE CONTRATACIONES EN BLOQUE #").append(blockIndex).append(" ---\n");
        // Asumiendo que getSmartContracts() devuelve la lista de contratos del bloque
        for (int i = 0; i < blk.countSmartContracts(); i++) {
            sb.append("\tContrato #").append(i + 1).append(": ")
              .append(blk.getSmartContract(i).toString()).append("\n");
        }
        return sb.toString();
    }

    public Block getLastBlock() {
        return chain.get(chain.size() - 1);
    }

    public List<Block> getChain() { 
        return chain; 
    }

    public int size() {
        return chain.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Block b : chain) {
            sb.append(b.toString()).append("\n");
        }
        return sb.toString();
    }
}