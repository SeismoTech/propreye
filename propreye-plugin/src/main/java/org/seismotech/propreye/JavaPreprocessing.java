package org.seismotech.propreye;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * A toolset for Java preprocessing.
 */
public class JavaPreprocessing {

  private static final String[][] prims = {
    {"boolean", "Boolean"},
    {"byte", "Byte"},
    {"short", "Short"},
    {"char", "Character"},
    {"int", "Integer"},
    {"long", "Long"},
    {"float", "Float"},
    {"double", "Double"},
  };
  private static final Map<String,String> low2up = new HashMap<>(prims.length);
  private static final Map<String,String> up2low = new HashMap<>(prims.length);
  static {
    for (final String[] pair: prims) {
      low2up.put(pair[0], pair[1]);
      up2low.put(pair[1], pair[0]);
    }
  }

  public String boxedType(String low) {
    final String up = low2up.get(low);
    if (up != null) return up;
    throw new IllegalArgumentException(
      "Unexpected unboxed type `" + low + "`");
  }

  public String unboxedType(String up) {
    final String low = up2low.get(up);
    if (low != null) return low;
    throw new IllegalArgumentException(
      "Unexpected boxed type `" + up + "`");
  }
}
