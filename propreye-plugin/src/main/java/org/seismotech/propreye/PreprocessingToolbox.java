package org.seismotech.propreye;

import java.util.Arrays;
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

  public List<Object> concat(List<Object> xs, List<Object> ys) {
    final ArrayList<Object> zs = new ArrayList<>(xs.size() + ys.size());
    zs.addAll(xs);
    zs.addAll(ys);
    return zs;
  }

  public List<Object> repeat(List<Object> xs, int n) {
    final ArrayList<Object> ys = new ArrayList<>(Math.max(0,n) * xs.size());
    for (int i = 0; i < n; i++) ys.addAll(xs);
    return ys;
  }

  public List<List<Object>> transpose(List<List<Object>> rows) {
    return transpose(rows, null);
  }

  public List<List<Object>> transpose(List<List<Object>> rows, Object absent) {
    final List<List<Object>> ts = new ArrayList<>();
    for (int j = 0;; j++) {
      final List<Object> col = new ArrayList<>();
      boolean some = false;
      for (final List<Object> row: rows) {
        final Object x;
        if (j < row.size()) {x = row.get(j); some = true;}
        else x = absent;
        col.add(x);
      }
      if (!some) break;
      ts.add(col);
    }
    return ts;
  }

  @SafeVarargs
  public final List<Map<String,Object>> table(List<String> colnames,
      List<Object>... rows) {
    return table(colnames, Arrays.asList(rows));
  }

  public List<Map<String,Object>> table(List<String> colnames,
      List<List<Object>> rows) {
    final int cols = colnames.size();
    final List<Map<String,Object>> table = new ArrayList<>(rows.size());
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
}
