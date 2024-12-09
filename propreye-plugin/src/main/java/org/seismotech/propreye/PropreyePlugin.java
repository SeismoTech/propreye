package org.seismotech.propreye;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;

public class PropreyePlugin implements Plugin<Project> {

  private static final String MAIN_INPUT_DIR
    = FileUtils.portable("src/main/templates");
  private static final String MAIN_OUTPUT_DIR
    = FileUtils.portable("generated/propreye/main/templates");
  private static final String TEST_INPUT_DIR
    = FileUtils.portable("src/test/templates");
  private static final String TEST_OUTPUT_DIR
    = FileUtils.portable("generated/propreye/test/templates");

  @Override
  public void apply(Project project) {
    final DirectoryProperty build = project.getLayout().getBuildDirectory();

    project.getTasks().register("propreyeMain", PropreyeTask.class).configure(
      tp -> {
        tp.setDescription("Preprocessing of main templates");
        tp.setInputDirectory(MAIN_INPUT_DIR);
        tp.setOutputDirectory(build.dir(MAIN_OUTPUT_DIR).get().getAsFile());
      });
    project.getTasks().register("propreyeTest", PropreyeTask.class).configure(
      tp -> {
        tp.setDescription("Preprocessing of test templates");
        tp.setInputDirectory(TEST_INPUT_DIR);
        tp.setOutputDirectory(build.dir(TEST_OUTPUT_DIR).get().getAsFile());
      });
  }
}
