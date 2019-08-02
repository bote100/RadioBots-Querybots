package net.bote.radiobots.querybots.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Elias Arndt | bote100
 * Created on 17.07.2019
 */

public class MapBuilder {

    public static Map<Object, Object> buildObjectMap(MapPair... pairs) {
        Map<Object, Object> map = new HashMap<>();
        Arrays.stream(pairs).forEach(pair -> map.put(pair.getLEFT(), pair.getRIGHT()));
        return map;
    }

    public static Map<String, String> buildStringMap(MapPair... pairs) {
        Map<String, String> map = new HashMap<>();
        Arrays.stream(pairs).forEach(pair -> map.put(String.valueOf(pair.getLEFT()), String.valueOf(pair.getRIGHT())));
        return map;
    }

}
