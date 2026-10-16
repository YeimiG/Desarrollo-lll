package blockchain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


class MiniJson {
    private MiniJson(){}
     public static String escribir(Object valor) {
        StringBuilder sb = new StringBuilder();
        escribir(sb, valor);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static void escribir(StringBuilder sb, Object v) {
        if (v == null) {
            sb.append("null");
        } else if (v instanceof String) {
            escribirCadena(sb, (String) v);
        } else if (v instanceof Double || v instanceof Float) {
            double d = ((Number) v).doubleValue();
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                throw new IllegalArgumentException("Numero no representable en JSON: " + d);
            }
            sb.append(Double.toString(d)); // ida y vuelta exacta con Double.parseDouble
        } else if (v instanceof Number || v instanceof Boolean) {
            sb.append(v.toString());
        } else if (v instanceof Map) {
            sb.append('{');
            boolean primero = true;
            for (Map.Entry<String, Object> e : ((Map<String, Object>) v).entrySet()) {
                if (!primero) sb.append(',');
                primero = false;
                escribirCadena(sb, e.getKey());
                sb.append(':');
                escribir(sb, e.getValue());
            }
            sb.append('}');
        } else if (v instanceof List) {
            sb.append('[');
            boolean primero = true;
            for (Object o : (List<Object>) v) {
                if (!primero) sb.append(',');
                primero = false;
                escribir(sb, o);
            }
            sb.append(']');
        } else {
            throw new IllegalArgumentException("Tipo no soportado: " + v.getClass());
        }
    }

    private static void escribirCadena(StringBuilder sb, String s) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        sb.append('"');
    }

    // ---------------------------------------------------------------- lectura
    public static Object leer(String texto) {
        if (texto == null) throw new IllegalArgumentException("JSON nulo");
        Lector l = new Lector(texto);
        l.espacios();
        Object v = l.valor(0);
        l.espacios();
        if (l.pos != texto.length()) throw new IllegalArgumentException("Texto sobrante tras el JSON");
        return v;
    }

    private static final class Lector {
        private static final int PROFUNDIDAD_MAX = 20; // evita desbordar la pila con JSON malicioso
        final String t;
        int pos = 0;

        Lector(String t) { this.t = t; }

        void espacios() {
            while (pos < t.length() && Character.isWhitespace(t.charAt(pos))) pos++;
        }

        char actual() {
            if (pos >= t.length()) throw new IllegalArgumentException("JSON incompleto");
            return t.charAt(pos);
        }

        void esperar(char c) {
            if (actual() != c) throw new IllegalArgumentException("Se esperaba '" + c + "' en la posicion " + pos);
            pos++;
        }

        Object valor(int prof) {
            if (prof > PROFUNDIDAD_MAX) throw new IllegalArgumentException("JSON demasiado anidado");
            espacios();
            char c = actual();
            if (c == '{') return objeto(prof);
            if (c == '[') return arreglo(prof);
            if (c == '"') return cadena();
            if (t.startsWith("true", pos)) { pos += 4; return Boolean.TRUE; }
            if (t.startsWith("false", pos)) { pos += 5; return Boolean.FALSE; }
            if (t.startsWith("null", pos)) { pos += 4; return null; }
            return numero();
        }

        Map<String, Object> objeto(int prof) {
            Map<String, Object> m = new LinkedHashMap<>();
            esperar('{');
            espacios();
            if (actual() == '}') { pos++; return m; }
            while (true) {
                espacios();
                String k = cadena();
                espacios();
                esperar(':');
                m.put(k, valor(prof + 1));
                espacios();
                char c = actual();
                pos++;
                if (c == '}') return m;
                if (c != ',') throw new IllegalArgumentException("Se esperaba ',' o '}' en la posicion " + (pos - 1));
            }
        }

        List<Object> arreglo(int prof) {
            List<Object> l = new ArrayList<>();
            esperar('[');
            espacios();
            if (actual() == ']') { pos++; return l; }
            while (true) {
                l.add(valor(prof + 1));
                espacios();
                char c = actual();
                pos++;
                if (c == ']') return l;
                if (c != ',') throw new IllegalArgumentException("Se esperaba ',' o ']' en la posicion " + (pos - 1));
            }
        }

        String cadena() {
            esperar('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = actual();
                pos++;
                if (c == '"') return sb.toString();
                if (c != '\\') { sb.append(c); continue; }
                char e = actual();
                pos++;
                switch (e) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'u':
                        if (pos + 4 > t.length()) throw new IllegalArgumentException("Escape \\u incompleto");
                        sb.append((char) Integer.parseInt(t.substring(pos, pos + 4), 16));
                        pos += 4;
                        break;
                    default: throw new IllegalArgumentException("Escape invalido: \\" + e);
                }
            }
        }

        Number numero() {
            int ini = pos;
            while (pos < t.length() && "+-0123456789.eE".indexOf(t.charAt(pos)) >= 0) pos++;
            String s = t.substring(ini, pos);
            if (s.isEmpty()) throw new IllegalArgumentException("Valor JSON invalido en la posicion " + ini);
            try {
                if (s.contains(".") || s.contains("e") || s.contains("E")) return Double.parseDouble(s);
                return Long.parseLong(s);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Numero invalido: " + s);
            }
        }
    }
}
