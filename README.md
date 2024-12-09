Propreye: preprocessing for Gradle.
======================================================================

**Propreye** is a preprocessing (template execution) plugin for Gradle.

The initial goal was to macro expand repetitive Java code dealing with
primitive types (typically, when you implement an algorithm on `int[]` and
then you need to repeat the same chunk of code for `long[]`, `short[]`, ...).
But this plugin can be used to preprocess any other programming language
or to do any other kind of preprocessing during project building.

This plugin defines a task `org.seismotech.propreye.PropreyeTask`
that expands all the templates defined in an input directory
and stores the result in an output directory,
preserving folder structure.

This plugin predefines 2 instances of `PropreyeTask`,
`propreyeMain` and `propreyeTest`, 
expanding templates at `src/main/templates` and `src/test/templates`,
respectively.
The result is stored somewhere at `build/generated`;
the exact place can be found out with `propreyeMain.outputDirectory`
and `propreyeTest.outputDirectory`, respectively.

Those tasks assume nothing about the template contents;
therefore, they are not linked to any other Gradle task.
To use them to preprocess code, they should be connected with the
compilation tasks.
For instance, to preprocess Java code, you should add the following declarations
to your `build.gradle`:
```
sourceSets {
  main {
    java {
      srcDir propreyeMain.outputDirectory
    }
  }
  test {
    java {
      srcDir propreyeTest.outputDirectory
    }
  }
}

compileJava {
  dependsOn propreyeMain
}

compileTestJava {
  dependsOn propreyeTest
}
```

Propreye plugin currently supports Apache Velocity templates,
although it is easy to add support (and we are willing to do) for other
templating engines like 
[ST4 (String Templates)](https://github.com/antlr/stringtemplate4),
[Apache FreeMarker](https://freemarker.apache.org/),
maybe [Rocker](https://github.com/fizzed/rocker),
...

Template engines are applied by file extension.
Currently, the following extensions are managed:

| Templating Engine | Extensions |
| --- | --- |
| Apache Velocity | `.vm` `.vt` `.vtl` |

A file with any other extension will be ignored.

See subproject [`propreye-case1`](propreye-case1/)
for a simple but realistic example.
