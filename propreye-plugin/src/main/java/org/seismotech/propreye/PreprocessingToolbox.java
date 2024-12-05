package org.seismotech.propreye;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * A toolset for Java preprocessing.
 */
public class PreprocessingToolbox {

  private final JavaPreprocessing jpp = new JavaPreprocessing();

  public JavaPreprocessing getJava() {return jpp;}

  public String upcase(String t) {return t.toUpperCase();}

  public String lowcase(String t) {return t.toLowerCase();}

  public String capitalize(String t) {
    return t.isEmpty() ? t
      : Character.toUpperCase(t.charAt(0)) + t.substring(1);
  }

  @SafeVarargs
  public final List<Map<String,Object>> table(List<String> colnames,
      List<Object>... rows) {
    final int cols = colnames.size();
    final List<Map<String,Object>> table = new ArrayList<>(rows.length);
    for (final List<Object> row: rows) {
      if (row.size() != cols) throw new IllegalArgumentException(
        "Table width mismatch: row has with " + row.size()
        + " for a table of width " + cols);
      final Map<String,Object> record = new HashMap<>(cols);
      for (int i = 0; i < cols; i++) {
        record.put(colnames.get(i), row.get(i));
      }
      table.add(record);
    }
    return table;
  }

  public List<Object> concat(List<Object> xs, List<Object> ys) {
    final ArrayList<Object> zs = new ArrayList<>(xs.size() + ys.size());
    zs.addAll(xs);
    zs.addAll(ys);
    return zs;
  }
}
