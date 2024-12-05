package org.seismotech.propreye;

import java.io.IOException;
import java.io.File;
import java.util.List;

public interface Preprocessor {

  String name();

  List<String> managedExtensions();

  void preprocess(File from, File to) throws IOException;
}
