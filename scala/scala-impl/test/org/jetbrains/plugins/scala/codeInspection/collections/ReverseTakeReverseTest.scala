package org.jetbrains.plugins.scala
package codeInspection
package collections

import com.intellij.testFramework.EditorTestUtil

/**
 * @author Nikolay.Tropin
 */
class ReverseTakeReverseTest extends OperationsOnCollectionInspectionTest {

  import EditorTestUtil.{SELECTION_END_TAG => END, SELECTION_START_TAG => START}

  override protected val classOfInspection: Class[_ <: OperationOnCollectionInspection] =
    classOf[ReverseTakeReverseInspection]

  override protected val hint: String =
    ScalaInspectionBundle.message("replace.reverse.take.reverse.with.takeRight")

  def test1(): Unit = {
    doTest(
      s"val n = 2; Seq().${START}reverse.take(n).reverse$END",
      "val n = 2; Seq().reverse.take(n).reverse",
      "val n = 2; Seq().takeRight(n)"
    )
  }
}
