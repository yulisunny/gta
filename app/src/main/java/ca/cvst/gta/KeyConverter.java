package ca.cvst.gta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KeyConverter {

    private static Map<String, String> table;
    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("co2", "Carbon Dioxide");
        aMap.put("co", "Carbon Monoxide");
        aMap.put("nox", "Nitrogen Oxides");
        aMap.put("aqhi", "Air Quality Health Index");
        aMap.put("o3", "Ozone");
        aMap.put("pm", "Particulate Matter");
        table = Collections.unmodifiableMap(aMap);
    }

    public static String toReadable(String key) {
        String rtn = table.get(key);
        if (rtn == null) {
            rtn = key;
        }
        return rtn;
    }

}
