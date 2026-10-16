package blockchain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class BlockCodec {
    private BlockCodec (){}
    
     public static String toJson(Block b) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", (long) b.getId());
        m.put("timeStamp", b.getTimeStamp());
        m.put("previousHash", b.getPreviousHash());
        m.put("hash", b.getHash());
        m.put("nonce", (long) b.getNonce());
        List<Object> contratos = new ArrayList<>();
        for (SmartContract c : b.getSmartContracts()) {
            Map<String, Object> cm = new LinkedHashMap<>();
            cm.put("idContrato", c.getIdContrato());
            cm.put("idEmpleado", c.getIdEmpleado());
            cm.put("nombreEmpleado", c.getNombreEmpleado());
            cm.put("puesto", c.getPuesto());
            cm.put("salario", c.getSalario());
            cm.put("sucursal", c.getSucursal());
            contratos.add(cm);
        }
        m.put("contratos", contratos);
        return MiniJson.escribir(m);
    }
    
    /** @throws IllegalArgumentException si el JSON no tiene la forma esperada. */
    @SuppressWarnings("unchecked")
    public static Block fromJson(String json) {
        Object raiz = MiniJson.leer(json);
        if (!(raiz instanceof Map)) throw new IllegalArgumentException("El bloque debe ser un objeto JSON");
        Map<String, Object> m = (Map<String, Object>) raiz;

        long id = entero(m, "id");
        long nonce = entero(m, "nonce");
        if (id < 0 || id > Integer.MAX_VALUE || nonce < Integer.MIN_VALUE || nonce > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("id o nonce fuera de rango");
        }
        long ts = entero(m, "timeStamp");
        String prev = texto(m, "previousHash");
        String hash = texto(m, "hash");

        Object lc = m.get("contratos");
        if (!(lc instanceof List)) throw new IllegalArgumentException("Falta la lista 'contratos'");
        List<SmartContract> contratos = new ArrayList<>();
        for (Object o : (List<Object>) lc) {
            if (!(o instanceof Map)) throw new IllegalArgumentException("Contrato invalido");
            Map<String, Object> cm = (Map<String, Object>) o;
            Object sal = cm.get("salario");
            if (!(sal instanceof Number)) throw new IllegalArgumentException("Falta 'salario'");
            contratos.add(new SmartContract(
                    texto(cm, "idContrato"), texto(cm, "idEmpleado"), texto(cm, "nombreEmpleado"),
                    texto(cm, "puesto"), ((Number) sal).doubleValue(), texto(cm, "sucursal")));
        }
        return Block.restaurar((int) id, ts, prev, hash, (int) nonce, contratos);
    }

    private static long entero(Map<String, Object> m, String campo) {
        Object v = m.get(campo);
        if (!(v instanceof Long)) throw new IllegalArgumentException("Campo entero invalido o ausente: " + campo);
        return (Long) v;
    }

    private static String texto(Map<String, Object> m, String campo) {
        Object v = m.get(campo);
        if (!(v instanceof String)) throw new IllegalArgumentException("Campo de texto invalido o ausente: " + campo);
        return (String) v;
    }
}
