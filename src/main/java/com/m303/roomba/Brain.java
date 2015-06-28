package com.m303.roomba;

import java.util.Map;

/**
 * Created by mley on 24.04.15.
 */
public interface Brain extends Constants{




    String think(String action, Map<String, Object> result);
}
