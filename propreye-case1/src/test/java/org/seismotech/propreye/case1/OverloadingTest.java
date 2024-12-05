package org.seismotech.propreye.case1;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OverloadingTest {

  @ParameterizedTest
  @MethodSource("overloadCounts")
  void overloadTest(int expected, Class<?> klass, String methodName) {
    final long found = Arrays.stream(klass.getDeclaredMethods())
      .filter(m -> m.getName().equals(methodName))
      .count();
    assertEquals(expected, found);
  }

  static List<Arguments> overloadCounts() {
    final List<Arguments> xs = new ArrayList<>();
    for (final String method: new String[] {
          "cmp", "lt", "le", "ge", "gt", "min", "max"}) {
      xs.add(Arguments.of(10, Comparisons.class, method));
    }
    xs.add(Arguments.of(9, ArrayEssences.class, "size"));
    xs.add(Arguments.of(18, ArrayEssences.class, "min"));
    xs.add(Arguments.of(18, ArrayEssences.class, "max"));
    return xs;
  }
}
