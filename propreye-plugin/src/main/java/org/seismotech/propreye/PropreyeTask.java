package org.seismotech.propreye;

import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.SourceTask;

@CacheableTask
public class PropreyeTask extends SourceTask {

  private static final String DEFAULT_INPUT_DIR
    = FileUtils.portable("/src/main/templates");
  private static final String DEFAULT_OUTPUT_DIR
    = FileUtils.portable("/build/generated/propreye/main/templates");

  private final Preprocessor pp;

  private File inputDir;
  private File outputDir;

  private List<File> invalidTemplates;

  public PropreyeTask() {
    this.pp = new MultiPreprocessor(new VTLPreprocessor(getLogger()));
    setGroup("propreye");
    include("**/*");
  }

  @InputDirectory
  @SkipWhenEmpty
  @Optional
  @PathSensitive(PathSensitivity.NONE) //Why NONE?
  public File getInputDirectory() {
    return inputDir.exists() ? inputDir : null;
  }

  @OutputDirectory
  public File getOutputDirectory() {
    return outputDir;
  }

  public PropreyeTask setInputDirectory(String inputDir) {
    return setInputDirectory(resolvedFile(inputDir));
  }

  public PropreyeTask setInputDirectory(File inputDir) {
    this.inputDir = inputDir;
    setSource(inputDir);
    return this;
  }

  public PropreyeTask setOutputDirectory(String outputDir) {
    return setOutputDirectory(resolvedFile(outputDir));
  }

  public PropreyeTask setOutputDirectory(File outputDir) {
    this.outputDir = outputDir;
    return this;
  }

  private File resolvedFile(String filename) {
    return new File(getProject().getProjectDir(), filename);
  }

  @TaskAction
  public void action() {
    getProject().delete(outputDir);
    invalidTemplates = new ArrayList<>();
    getSource().visit(this::preprocess);
    if (!invalidTemplates.isEmpty()) {
      throw new PropreyeTaskException(
        "There are invalid templates: " + invalidTemplates);
    }
  }

  private void preprocess(FileVisitDetails fidet) {
    if (fidet.isDirectory()) return;
    final File from = fidet.getFile();
    final String ext = FileUtils.extension(from);
    if (!pp.managedExtensions().contains(ext)) {
      getLogger().debug("Ignoring " + from);
    } else {
      getLogger().debug("Preprocessing " + from);
      final File to
        = new File(outputDir, FileUtils.dropExtension(fidet.getPath()));
      try {
        to.getParentFile().mkdirs();
        pp.preprocess(from, to);
      } catch (Exception e) {
        invalidTemplates.add(from);
        //Not using exception because the preprocessor should have logged
        //that info
        getLogger().log(LogLevel.ERROR, "Cannot process template " + from);
      }
    }
  }
}
