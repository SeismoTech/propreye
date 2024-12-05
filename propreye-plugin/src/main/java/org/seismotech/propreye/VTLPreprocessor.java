package org.seismotech.propreye;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.InvalidReferenceEventHandler;
import org.apache.velocity.app.event.implement.ReportInvalidReferences;
import org.apache.velocity.app.event.implement.InvalidReferenceInfo;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.util.introspection.Info;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.LogLevel;

/**
 * A preprocessor with Apache Velocity.
 *
 * Bounds {@link PreprocessingToolbox} to {@code ppy}.
 * An observation about that binding:
 * Velocity can call instance methods in an instance
 * or static methods in a class.
 * All methods in {@link PreprocessingToolbox} are instance methods,
 * therefore an instance is bound in {@code Context ctx}
 * with {@code ctx.put("jpp", new PreprocessingToolbox())}.
 * Was {@link PreprocessingToolbox} organized as a set of static methods,
 * it should be installed with
 * {@code ctx.put("jpp", PreprocessingToolbox.class)}.
 */
public class VTLPreprocessor implements Preprocessor {

  private static final List<String> EXTS
    = Collections.unmodifiableList(Arrays.asList("vm", "vt", "vtl"));

  private final Logger logger;
  private final VelocityEngine ve;

  public VTLPreprocessor(Logger logger) {
    this.logger = logger;
    final Properties prop = new Properties();
    // Use defaults value, except resouce loader base:
    // prop.setProperty("resource.loaders", "file");
    // prop.setProperty("resource.loader.file.description", "Velocity File Resource Loader");
    // prop.setProperty("resource.loader.file.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
    // prop.setProperty("resource.loader.file.cache", "false");
    // prop.setProperty("resource.loader.file.modification_check_interval", "0");
    prop.setProperty("resource.loader.file.path", "/");
    this.ve = new VelocityEngine(prop);
    ve.init();
  }

  @Override public String name() {return "Apache Velocity Preprocessor";}

  @Override
  public List<String> managedExtensions() {return EXTS;}

  @Override
  public void preprocess(File from, File to) throws IOException {
    final String tempName = from.toString();
    final Template temp = ve.getTemplate(tempName);
    final Context ctxt = new VelocityContext();
    ctxt.put("ppy", new PreprocessingToolbox());
    final EventCartridge ec = new EventCartridge();

    //Not using Apache Velocity reference error reporting handler
    // because its messages have very little information.
    //final ReportInvalidReferences report = new ReportInvalidReferences();
    //ec.addEventHandler(report);

    final InvalidReferenceCollector report = new InvalidReferenceCollector();
    ec.addEventHandler(report);

    ec.attachToContext(ctxt);
    try (final FileWriter out = new FileWriter(to)) {
      temp.merge(ctxt, out);
    }

    // final List<InvalidReferenceInfo> errors = report.getInvalidReferences();
    // if (!errors.isEmpty()) {
    //   for (final InvalidReferenceInfo error: errors) {
    //     logger.log(LogLevel.ERROR, error.toString());
    //   }
    //   throw new RuntimeException();
    // }

    final List<InvalidReference> errors = report.errors();
    for (final InvalidReference error: errors) {
      logger.log(LogLevel.ERROR, error.toString());
    }
    report.throwFirstAsParseError();
  }

  //----------------------------------------------------------------------
  private static class InvalidReferenceCollector
    implements InvalidReferenceEventHandler {

    private List<InvalidReference> errors = new ArrayList<>();

    public List<InvalidReference> errors() {return errors;}

    public void throwFirstAsParseError() {
      if (!errors.isEmpty()) errors.get(0).throwAsParseError();
    }

    public Object invalidGetMethod(Context context, String reference,
        Object object, String property, Info info) {
      error(
        property == null
        ? "Cannot find reference `" + reference + "'"
        : "Cannot get property `" + property + "`"
          + " from an object of type `" + typeName(object) + "`"
          + " at expression `" + reference + "`",
        info);
      return null;
    }

    public Object invalidMethod(Context context, String reference,
        Object object, String method, Info info) {
      error(
        "Cannot call method `" + method + "`"
        + " on an object of type `" + typeName(object) + "`"
        + " at expression `" + reference + "`",
        info);
      return null;
    }

    public boolean invalidSetMethod(Context context, String leftreference,
        String rightreference, Info info) {
      //The message is not using rightreference because sometimes it has
      //a very strange and confusing content (for instance, a `)`)
      error(
        "Cannot set `" + leftreference /*+ "` with `" + rightreference + "`"*/,
        info);
      return true;
    }

    private void error(String msg, Info info) {
      errors.add(new InvalidReference(msg, info));
    }

    private static String typeName(Object obj) {
      return obj == null ? "Null" : obj.getClass().getName();
    }
  }

  private static class InvalidReference {
    private final String msg;
    private final Info info;

    public InvalidReference(String msg, Info info) {
      this.msg = msg;
      this.info = info;
    }

    public void throwAsParseError() {
      throw new ParseErrorException(msg, info);
    }

    public String toString() {
      return msg + " (" + info + ")";
    }
  }
}
