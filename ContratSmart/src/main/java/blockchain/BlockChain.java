package blockchain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockChain {

    /** Hash previo fijo del bloque genesis. */
    public static final String GENESIS_PREVIOUS_HASH =
            "0000000000000000000000000000000000000000000000000000000000000000";
    /** Marca de tiempo fija del genesis (identica en todos los nodos). */
    public static final long GENESIS_TIMESTAMP = 0L;

    /** Resultado de intentar agregar un bloque a la cadena. */
    public enum Resultado {
        /** El bloque era valido y consecutivo: se agrego. */
        ACEPTADO,
        /** El bloque ya estaba en la cadena (mismo hash en su posicion). No se vuelve a agregar. */
        DUPLICADO,
        /** El bloque es invalido (hash alterado, no cumple dificultad, datos nulos...). */
        INVALIDO,
        /** El bloque es valido pero salta bloques: faltan bloques intermedios (hay que sincronizar). */
        FUERA_DE_ORDEN,
        /** El bloque compite con otro de la misma altura o no enlaza con nuestro ultimo bloque (fork). */
        CONFLICTO
    }
    private final List<Block> chain;
    private final int complexity;
    private final String proofOfWork;
    private final String genesisHash;

    public BlockChain(int complexity, String proofOfWork) {
        this.chain = new ArrayList<>();
        this.complexity = complexity;
        this.proofOfWork = proofOfWork;
        this.genesisHash = createGenesis(); // Regla 9
    }
    // Regla 9:El bloque génesis es IGUAL en todos los nodos (mismos datos => mismo hash).
    // Se mina ANTES de agregarlo a la cadena (nunca hay un genesis sin minar dentro de ella).
    private String  createGenesis() {
       Block genesis = new Block(0, GENESIS_PREVIOUS_HASH, GENESIS_TIMESTAMP);
        mineBlock(genesis);
        this.chain.add(genesis);
        return genesis.getHash();
    }

    // Regla 10: Crea un nuevo bloque enlazado al último bloque ACEPTADO
    // NO lo agrega a la cadena: el llamador lo llena, lo mina y luego llama a addBlock().
    public Block createBlock() {
        Block lastBlock = getLastBlock();
        Block newBlock = new Block(
                lastBlock.getId() +1, lastBlock.getHash()
        );
        return newBlock;
        /*Block newBlock = new Block(chain.size(), lastBlock.getHash());
        this.chain.add(newBlock);
        return newBlock;*/
    }

    // Regla 3 y 6: Minado del bloque mediante prueba de trabajo (Proof of Work)
    public void mineBlock(Block block) {
        String target = target();
        while (!block.getHash().startsWith(target)) {
            block.setNonce(block.getNonce() + 1);
            block.setHash(block.generateHash());
        }
        /**String target = new String(new char[complexity]).replace('\0', proofOfWork.charAt(0));
        while (!block.getHash().substring(0, complexity).equals(target)) {
            block.setNonce(block.getNonce() + 1);
            block.setHash(block.generateHash());
        }**/
    }
    /** Prefijo que debe tener un hash valido, p.ej. "0000" para dificultad 4. */
    private String target() {
        return new String(new char[complexity]).replace('\0', proofOfWork.charAt(0));
    }
   // comprueba que el bloque de forma autonoma
    public boolean isSelfConsistent(Block block) {
        return block != null
                && block.getHash() != null
                && block.getPreviousHash() != null
                && block.getSmartContracts() != null
                && block.getHash().equals(block.generateHash())
                && block.getHash().startsWith(target());
    }
    
    //Valida una cadena candidata 
    public boolean isValidChain(List<Block> candidate) {
        if (candidate == null || candidate.isEmpty()) return false;
        Block first = candidate.get(0);
        if (!isValidBlock(first, null) || !genesisHash.equals(first.getHash())) return false;
        for (int i = 1; i < candidate.size(); i++) {
            if (!isValidBlock(candidate.get(i), candidate.get(i - 1))) return false;
        }
        return true;
    }
    /** Valida toda la cadena aceptada actual (util tras restaurar de disco). */
    public synchronized boolean isChainValid() {
        return isValidChain(chain);
    }
    /**
     * Agrega un bloque ya minado SOLO si corresponde. Devuelve por que se acepto o rechazo.
     * Garantiza: un bloque invalido no entra; un bloque valido entra una sola vez.
     */
    public synchronized Resultado addBlock(Block block) {
        if (!isSelfConsistent(block)) return Resultado.INVALIDO;
        Block last = getLastBlock();

        if (block.getId() <= last.getId()) {
            // Ya tenemos un bloque en esa altura: o es el mismo (duplicado) o compite (fork).
            if (block.getId() < 0) return Resultado.INVALIDO;
            Block mine = chain.get(block.getId());
            return mine.getHash().equals(block.getHash()) ? Resultado.DUPLICADO : Resultado.CONFLICTO;
        }
        if (block.getId() > last.getId() + 1) {
            return Resultado.FUERA_DE_ORDEN;
        }
        // id == last.id + 1
        if (!isValidBlock(block, last)) {
            return Resultado.CONFLICTO; // es consecutivo pero no enlaza con nuestro ultimo hash
        }
        chain.add(block);
        return Resultado.ACEPTADO;
    }
    //Valida un  bloque frente al bloque anterior esperando: no nulo, nohay anterior, si hay 
    public boolean isValidBlock(Block block, Block previousBlock) {
        if (!isSelfConsistent(block)) return false;
        if (previousBlock == null) {
            return block.getId() == 0 && GENESIS_PREVIOUS_HASH.equals(block.getPreviousHash());
        }
        return block.getId() == previousBlock.getId() + 1
                && block.getPreviousHash().equals(previousBlock.getHash());
    }
    
    
    // Nombre original conservado: valida y agrega. true solo si el bloque fue ACEPTADO.
    public boolean addProvedBlock(Block block) {
        return addBlock(block) == Resultado.ACEPTADO;
        /*    String target = new String(new char[complexity]).replace('\0', proofOfWork.charAt(0));
        
        // Verifica que el bloque apunte al hash correcto y cumpla la dificultad
        if (block.getPreviousHash().equals(getLastBlock().getHash()) && 
            block.getHash().substring(0, complexity).equals(target) &&
            block.getHash().equals(block.generateHash())) {
            
            this.chain.add(block);
            return true;
        }
        return false;
    */
    }
    //Regla del concenso "cadena valida mas larga "
     public synchronized List<Block> replaceChainIfBetter(List<Block> candidate) {
        if (!isValidChain(candidate)) return null;
        Block candTip = candidate.get(candidate.size() - 1);
        Block ourTip = getLastBlock();
        boolean better = candidate.size() > chain.size()
                || (candidate.size() == chain.size() && candTip.getHash().compareTo(ourTip.getHash()) < 0);
        if (!better) return null;

        Set<String> newHashes = new HashSet<>();
        for (Block b : candidate) newHashes.add(b.getHash());
        List<Block> orphans = new ArrayList<>();
        for (Block b : chain) {
            if (!newHashes.contains(b.getHash())) orphans.add(b);
        }
        chain.clear();
        chain.addAll(candidate);
        return orphans;
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

    public synchronized Block getLastBlock() {
        return chain.get(chain.size() - 1);
    }

    //devuelve una copia de la cadena aseptada
    public synchronized List<Block> getChain(){
        return new ArrayList<>(chain);
    }
    public synchronized int size() {
        return chain.size();
    }

    public int getComplexity() { return complexity; }

    public String getGenesisHash() { return genesisHash; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Block b : chain) {
            sb.append(b.toString()).append("\n");
        }
        return sb.toString();
    }
}