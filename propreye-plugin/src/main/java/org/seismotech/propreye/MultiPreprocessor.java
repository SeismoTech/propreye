package org.seismotech.propreye;

import java.io.IOException;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class MultiPreprocessor implements Preprocessor {

  private final Preprocessor[] delegates;
  private final List<String> exts;
  private final Map<String,Preprocessor> ext2pp;

  public MultiPreprocessor(Preprocessor... delegates) {
    this.delegates = delegates;
    this.exts = allExtensions(delegates);
    this.ext2pp = byExtension(delegates);
  }

  public static List<String> allExtensions(Preprocessor... delegates) {
    // return Arrays.stream(delegates)
    //   .flatMap(pp -> pp.managedExtensions().stream())
    //   .toList();
    final List<String> all = new ArrayList<>();
    for (final Preprocessor pp: delegates) all.addAll(pp.managedExtensions());
    return Collections.unmodifiableList(all);
  }

  public static Map<String,Preprocessor> byExtension(Preprocessor... delegates){
    final Map<String,Preprocessor> ext2pp = new HashMap<>();
    for (final Preprocessor pp: delegates) {
      for (final String ext: pp.managedExtensions()) {
        final Preprocessor pp0 = ext2pp.get(ext);
        if (pp0 != null) throw new IllegalArgumentException(
          "Extension " + ext + " managed by " + pp0.name()
          + " and " + pp.name());
        ext2pp.put(ext, pp);
      }
    }
    return ext2pp;
  }

  @Override
  public String name() {
    return "Preprocessor by extension delegating to" + Arrays.stream(delegates)
      .map(Preprocessor::name).collect(Collectors.joining(", "));
  }

  @Override
  public List<String> managedExtensions() {return exts;}

  @Override
  public void preprocess(File from, File to) throws IOException {
    final String ext = FileUtils.extension(from);
    final Preprocessor pp = ext2pp.get(ext);
    if (pp == null) throw new IllegalArgumentException(
      "Cannot manage extension " + ext);
    pp.preprocess(from, to);
  }
}
