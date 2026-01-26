package org.seismotech.propreye;

import java.io.File;

public class FileUtils {

  public static String portable(String filename) {
    return filename.replace("/", File.separator);
  }

  public static String extension(File filename) {
    final String name = filename.getName();
    final int dot = name.lastIndexOf('.');
    return dot == -1 ? null : name.substring(dot+1);
  }

  public static String dropExtension(String filename) {
    final int dot = filename.lastIndexOf('.');
    return dot == -1 ? filename : filename.substring(0,dot);
  }

  public static File dropExtension(File filename) {
    return new File(filename.getParentFile(),
        dropExtension(filename.getName()));
  }
}
