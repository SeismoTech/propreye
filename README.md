This a preprocessing (template execution) plugin for Gradle.

The initial goal was to macro expand repetitive Java code dealing with
primitive types (typically, when you implement an algorithm on `int[]` and
then you need to repeat the same chunk of code for `long[]`, `short[]`, ...).
But this plugin can be used to preprocess any other programming language
or to do any other kind of preprocessing during compilation.

This plugin defines a task `org.seismotech.propreye.PropreyeTask`
that expands all the templates defined in an input directory
and stores the result in an output directory,
preserving folder structure.

This pluging predefines 2 instances of `PropreyeTask`,
`propreyeMain` and `propreyeTest`, 
expanding emplates at `src/main/templates` and `src/test/templates`,
respectivelly .
The result is stores some where at `build/generated`;
the exact place can be find out with `propreyeMain.outputDirectory`
and `propreyeTest.outputDirectory`.

Those tasks assume nothing about the template contents;
therefore, they are not linked to other (typical) Gradle task.
To be use them to preprocess code, they should be connected with the
compilation task.
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
  dependsOn propreyeTestGround, propreyeTest
}
```

Currently, it supports Apache Velocity templates,
although it is easy to add (and we are willing to do) support for other
templating engines like ST4 (String Templates), Apache FreeMarker, ...

Template engines are applied by file extension.
Currently, the following extensions are managed:

| Templating Engine | Extensions |
| --- | --- |
| Apache Velocity | `.vm` `.vt` `.vtl` |

A file with any other extension will be ignored.

Subproject [`propreye-case1`](propreye-case1/README.md)
is a simple but realistic example.
