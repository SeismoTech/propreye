Use case 1: Operations on primitive types
======================================================================

This subproject is an example of how to use Propreye plugin to expand
code for all the primitive types of Java avoiding code repetition.

Class [`Comparisons`](
src/main/templates/org/seismotech/propreye/case1/Comparisons.java.vm)
comprises comparison methods for all the primitive
types and for `Comparable<T>`s.
The goal is to have an uniform notation for all the types.
With that base, it is trivial to implement general `min` and `max` templates
for all primitive arrays and array of `Comparable<T>`;
that is done in [`ArrayEssences`](
src/main/templates/org/seismotech/propreye/case1/ArrayEssences.java.vm).

Preprocessing is also used to test those clases.
See [`ComparisonsTest`](
src/test/templates/org/seismotech/propreye/case1/ComparisonsTest.java.vm)
and [`ArrayEssencesTest`](
src/test/templates/org/seismotech/propreye/case1/ArrayEssencesTest.java.vm)
