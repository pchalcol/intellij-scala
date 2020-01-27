package org.jetbrains.plugins.scala.lang.completion.ml

import java.util

import com.intellij.codeInsight.completion.ml.{ContextFeatures, ElementFeatureProvider, MLFeatureValue}
import com.intellij.codeInsight.completion.{CodeCompletionHandlerBase, CompletionLocation, CompletionType}
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiNamedElement
import org.jetbrains.plugins.scala.ScalaLanguage
import org.jetbrains.plugins.scala.base.ScalaLightCodeInsightFixtureTestAdapter
import org.jetbrains.plugins.scala.extensions._

import scala.collection.mutable

class ScalaElementFeatureProviderTest extends ScalaLightCodeInsightFixtureTestAdapter {

  def testPostfix(): Unit = {

    assertContext("postfix", MLFeatureValue.binary(true))(
      """object X {
        |  val a = 1
        |  a <caret>
        |}
        |""".stripMargin
    )

    assertContext("postfix", MLFeatureValue.binary(false))(
      """object X {
        |  val a = 1
        |  a.<caret>
        |}
        |""".stripMargin
    )
  }

  def testInsideCatch(): Unit = {

    assertContext("inside_catch", MLFeatureValue.binary(true))(
      """object X {
        |  try a
        |  catch <caret>
        |}
        |""".stripMargin
    )

    assertContext("inside_catch", MLFeatureValue.binary(false))(
      """object X {
        |  try <caret>
        |  catch b
        |}
        |""".stripMargin
    )
  }

  def testTypeExpected(): Unit = {
    assertContext("type_expected", MLFeatureValue.binary(true))(
      """object X {
        |  List[<caret>]
        |}
        |""".stripMargin
    )

    assertContext("type_expected", MLFeatureValue.binary(true))(
      """object X {
        |  type A = <caret>
        |}
        |""".stripMargin
    )

    assertContext("type_expected", MLFeatureValue.binary(true))(
      """object X {
        |  val a: <caret>
        |}
        |""".stripMargin
    )

    assertContext("type_expected", MLFeatureValue.binary(true))(
      """object X {
        |  class A extends <caret>
        |}
        |""".stripMargin
    )

    assertContext("type_expected", MLFeatureValue.binary(true))(
      """object X {
        |  type A = Int with <caret>
        |}
        |""".stripMargin
    )

    assertContext("type_expected", MLFeatureValue.binary(true))(
      """object X {
        |  1 match { case _: <caret> }
        |}
        |""".stripMargin
    )

    assertContext("type_expected", MLFeatureValue.binary(true))(
      """object X {
        |  def f(): <caret>
        |}
        |""".stripMargin
    )

    assertContext("type_expected", MLFeatureValue.binary(false))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )

    assertContext("type_expected", MLFeatureValue.binary(false))(
      """object X {
        |  val a = <caret>
        |}
        |""".stripMargin
    )

    assertContext("type_expected", MLFeatureValue.binary(false))(
      """object X {
        |  1.<caret>
        |}
        |""".stripMargin
    )
  }

  def testAfterNew(): Unit = {
    assertContext("after_new", MLFeatureValue.binary(true))(
      """object X {
        |  new <caret>
        |}
        |""".stripMargin
    )

    assertContext("after_new", MLFeatureValue.binary(false))(
      """object X {
        |  new java.util.HashMap(<caret>)
        |}
        |""".stripMargin
    )
  }

  def testKind(): Unit = {
    import CompletionItem._

    assertElement("kind", "type", MLFeatureValue.categorical(KEYWORD))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("kind", "Nil", MLFeatureValue.categorical(VALUE))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("kind", "BufferedIterator", MLFeatureValue.categorical(TYPE_ALIAS))(
      """object X {
        |  type a = <caret>
        |}
        |""".stripMargin
    )

    assertElement("kind", "a", MLFeatureValue.categorical(VARIABLE))(
      """object X {
        |  var a = 1
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("kind", "X", MLFeatureValue.categorical(OBJECT))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("kind", "X", MLFeatureValue.categorical(CLASS))(
      """class X {
        |  type a = <caret>
        |}
        |""".stripMargin
    )

    assertElement("kind", "X", MLFeatureValue.categorical(TRAIT))(
      """trait X {
        |  type a = <caret>
        |}
        |""".stripMargin
    )

    assertElement("kind", "int2Integer", MLFeatureValue.categorical(FUNCTION))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("kind", "asInstanceOf", MLFeatureValue.categorical(SYNTHETHIC_FUNCTION))(
      """object X {
        |  "" <caret>
        |}
        |""".stripMargin
    )


    assertElement("kind", "LinkageError", MLFeatureValue.categorical(EXCEPTION))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("kind", "java", MLFeatureValue.categorical(PACKAGE))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )
  }

  def testKeyword(): Unit = {
    import Keyword._

    assertElement("keyword", "import", MLFeatureValue.categorical(IMPORT))(
      """<caret>
        |""".stripMargin
    )

    assertElement("keyword", "import", MLFeatureValue.categorical(IMPORT))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("keyword", "val", MLFeatureValue.categorical(VAL))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("keyword", "var", MLFeatureValue.categorical(VAR))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("keyword", "class", MLFeatureValue.categorical(CLASS))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("keyword", "if", MLFeatureValue.categorical(IF))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("keyword", "if", MLFeatureValue.categorical(IF))(
      """object X {
        |  if (true) {
        |  }
        |  else <caret>
        |}
        |""".stripMargin
    )

    assertElement("keyword", "else", MLFeatureValue.categorical(ELSE))(
      """object X {
        |  if (true) {
        |  }
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("keyword", "yield", MLFeatureValue.categorical(YIELD))(
      """object X {
        |  for {
        |    _ <- List.empty
        |  }
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("keyword", "else", MLFeatureValue.categorical(ELSE))(
      """object X {
        |  if (true) {
        |  }
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("keyword", "try", MLFeatureValue.categorical(TRY))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("keyword", "catch", MLFeatureValue.categorical(CATCH))(
      """object X {
        |  try {
        |  }
        |  <caret>
        |}
        |""".stripMargin
    )
  }

  def testSymbolic(): Unit = {
    assertElement("symbolic", "+", MLFeatureValue.binary(true))(
      """object X {
        |  Set.empty <caret>
        |}
        |""".stripMargin
    )

    assertElement("symbolic", "--", MLFeatureValue.binary(true))(
      """object X {
        |  Set.empty <caret>
        |}
        |""".stripMargin
    )

    assertElement("symbolic", "contains", MLFeatureValue.binary(false))(
      """object X {
        |  Set.empty <caret>
        |}
        |""".stripMargin
    )
  }

  def testUnary(): Unit = {
    assertElement("unary", "unary_+", MLFeatureValue.binary(true))(
      """object X {
        |  1 <caret>
        |}
        |""".stripMargin
    )

    assertElement("unary", "+", MLFeatureValue.binary(false))(
      """object X {
        |  1 <caret>
        |}
        |""".stripMargin
    )
  }

  def testScala(): Unit = {

    assertElement("scala", "List", MLFeatureValue.binary(true))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("scala", "NoSuchMethodError", MLFeatureValue.binary(false))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )
  }

  def testJavaObjectMethod(): Unit = {

    assertElement("java_object_method", "equals", MLFeatureValue.binary(true))(
      """object X {
        |  1 <caret>
        |}
        |""".stripMargin
    )

    assertElement("java_object_method", "+", MLFeatureValue.binary(false))(
      """object X {
        |  1 <caret>
        |}
        |""".stripMargin
    )

    assertElement("java_object_method", "to", MLFeatureValue.binary(false))(
      """object X {
        |  1 <caret>
        |}
        |""".stripMargin
    )
  }

  def testArgumentCount(): Unit = {

    assertElement("argument_count", "Nil", MLFeatureValue.float(-1.0))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("argument_count", "f", MLFeatureValue.float(0.0))(
      """object X {
        |  def f(): Unit = ???
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("argument_count", "print", MLFeatureValue.float(1.0))(
      """object X {
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("argument_count", "f", MLFeatureValue.float(2))(
      """object X {
        |  val f: (Int, Int) => Double = ???
        |  <caret>
        |}
        |""".stripMargin
    )

    assertElement("argument_count", "f", MLFeatureValue.float(0))(
      """object X {
        |  def f(implicit int: Int): Unit = ???
        |  <caret>
        |}
        |""".stripMargin
    )
  }

  def testNameNameDist(): Unit = {

    assertElement("name_name_sim", "List", MLFeatureValue.float(-1.0))(
      """object X {
        |  val l = <caret>
        |}
        |""".stripMargin
    )

    assertElement("name_name_sim", "List", MLFeatureValue.float(1.0))(
      """object X {
        |  val list = <caret>
        |}
        |""".stripMargin
    )

    assertElement("name_name_sim", "ScalaReflectionException", MLFeatureValue.float(1.0))(
      """object X {
        |  var scalaReflectionException = <caret>
        |}
        |""".stripMargin
    )

    assertElement("name_name_sim", "ind", MLFeatureValue.float(0.6))(
      """object X {
        |  val ind = ???
        |  "".charAt(index = <caret>)
        |}
        |""".stripMargin
    )

    assertElement("name_name_sim", "List", MLFeatureValue.float(0.5))(
      """object X {
        |  def byteList = <caret>
        |}
        |""".stripMargin
    )

    assertElement("name_name_sim", "List", MLFeatureValue.float(0.25))(
      """object X {
        |  type ByteListTypeName = <caret>
        |}
        |""".stripMargin
    )

    assertElement("name_name_sim", "List", MLFeatureValue.float(0.5))(
      """object X {
        |  def f(listname: Nothing) = ???
        |  f(<caret>)
        |}
        |""".stripMargin
    )

    // TODO for some reason expectedTypeEx don't return name for non local methods
//    assertElement("name_name_sim", "requ", MLFeatureValue.float(0.5))(
//      """object X {
//        |  val requ = ???
//        |  require(<caret>)
//        |}
//        |""".stripMargin
//    )
  }

  def testNameTypeDist(): Unit = {

    assertElement("name_type_sim", "Array", MLFeatureValue.float(-1.0))(
      """object X {
        |  type A = <caret>
        |}
        |""".stripMargin
    )

    assertElement("name_type_sim", "List", MLFeatureValue.float(0.25))(
      """object X {
        |  type ByteListTypeName = <caret>
        |}
        |""".stripMargin
    )

    assertElement("name_type_sim", "Product12", MLFeatureValue.float(1.0))(
      """object X {
        |  def f(product11: <caret>)
        |}
        |""".stripMargin
    )

    assertElement("name_type_sim", "List", MLFeatureValue.float(1.0))(
      """object X {
        |  val list: <caret>
        |}
        |""".stripMargin
    )

    assertElement("name_type_sim", "a", MLFeatureValue.float(0.5))(
      """object X {
        |  val a: List[Int] = ???
        |  def f(listname: Nothing) = ???
        |  f(<caret>)
        |}
        |""".stripMargin
    )
  }

  def testTypeNameDist(): Unit = {

    assertElement("type_name_sim", "Array", MLFeatureValue.float(-1.0))(
      """object X {
        |  val a = <caret>
        |}
        |""".stripMargin
    )

    assertElement("type_name_sim", "integral", MLFeatureValue.float(0.375))(
      """object X {
        |  val integral = ???
        |  val b: Int = <caret>
        |}
        |""".stripMargin
    )

    assertElement("type_name_sim", "index", MLFeatureValue.float(0.4))(
      """object X {
        |  val index = ???
        |  "".charAt(<caret>)
        |}
        |""".stripMargin
    )

    // TODO for some reason expectedTypeEx don't return any type for overloading
//    assertElement("name_name_sim", "requ", MLFeatureValue.float(0.5))(
//      """object X {
//        |  val string = ???
//        |  "".indexOf(<caret>)
//        |}
//        |""".stripMargin
//    )
  }

  def testTypeTypeDist(): Unit = {

    assertElement("type_type_sim", "integer", MLFeatureValue.float(-1.0))(
      """object X {
        |  type I = Int
        |  val integer: Int = ???
        |  val anotherInteger: I = <caret>
        |}
        |""".stripMargin
    )

    assertElement("type_type_sim", "a", MLFeatureValue.float(0.5))(
      """object X {
        |  val a: Option[Int] = ???
        |  var b: Int with Double = <caret>
        |}
        |""".stripMargin
    )

    assertElement("type_type_sim", "a", MLFeatureValue.float(1.0))(
      """object X {
        |  val a: Option[Int] = ???
        |  def f(o: Option[Int]) = ???
        |  f(<caret>)
        |}
        |""".stripMargin
    )

    assertElement("type_type_sim", "a", MLFeatureValue.float(1.0))(
      """object X {
        |  val a: Option[Int] = ???
        |  val b: Int = <caret>
        |}
        |""".stripMargin
    )

    assertElement("type_type_sim", "true", MLFeatureValue.float(1.0))(
      """object X {
        |  def f(x: Boolean): Unit = ???
        |  f(<caret>)
        |}
        |""".stripMargin
    )
  }

  private def assertContext(name: String, expected: MLFeatureValue)(fileText : String): Unit = {
    val elementsFeatures = computeElementsFeatures(fileText)
    AssertFeatureValues.equals(expected, elementsFeatures.head._2.get(name))
  }

  private def assertElement(name: String, element: String, expected: MLFeatureValue)(fileText : String): Unit = {
    val elementFeatures = computeElementsFeatures(fileText)
    AssertFeatureValues.equals(expected, elementFeatures(element).get(name))
  }

  private def computeElementsFeatures(fileText: String): Map[String, util.Map[String, MLFeatureValue]] = {
    class ScalaElementFeatureProviderWrapper extends ElementFeatureProvider {

      private val original = new ScalaElementFeatureProvider

      val elements = mutable.Map.empty[String, util.Map[String, MLFeatureValue]]

      override def getName: String = original.getName

      override def calculateFeatures(element: LookupElement, location: CompletionLocation, contextFeatures: ContextFeatures): util.Map[String, MLFeatureValue] = {
        val result = original.calculateFeatures(element, location, contextFeatures)

        val name = element.getObject match {
          case named: PsiNamedElement => named.name
          case _ => element.getObject.toString
        }

        elements += name -> result

        result
      }
    }

    val provider = new ScalaElementFeatureProviderWrapper
    try {
      ElementFeatureProvider.EP_NAME.addExplicitExtension(ScalaLanguage.INSTANCE, provider)

      configureFromFileText(fileText)
      changePsiAt(getEditor.getCaretModel.getOffset)
      getFixture.complete(CompletionType.BASIC, 1)

      val handler = new CodeCompletionHandlerBase(CompletionType.BASIC, false, false, true)
      handler.invokeCompletion(getProject, getEditor, 1)

      provider.elements.toMap
    }
    finally {
      ElementFeatureProvider.EP_NAME.removeExplicitExtension(ScalaLanguage.INSTANCE, provider)
    }
  }
}
