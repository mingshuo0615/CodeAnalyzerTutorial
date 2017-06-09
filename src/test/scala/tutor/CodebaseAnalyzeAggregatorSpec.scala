package tutor

import org.scalatest.{FeatureSpec, GivenWhenThen, ShouldMatchers}
import tutor.utils.FileUtil

class CodebaseAnalyzeAggregatorSpec extends FeatureSpec with ShouldMatchers with GivenWhenThen {

  val codeBaseAnalyzeAggregator = new CodebaseAnalyzeAggregator {}
  val aInfo: SourceCodeInfo = SourceCodeInfo("a.scala", "a.scala", 10)
  val bInfo: SourceCodeInfo = SourceCodeInfo("b.scala", "b.scala", 5)

  feature("aggregate the result of individual source code analyze") {
    scenario("count file numbers by type") {
      Given("a folder contains 2 .scala file and a .sbt file and a file without ext")
      val ls = List("a.scala", "b.scala", "c.sbt", "d")
      When("analyze that folder")
      Then("result should contain 2 scala file, 1 sbt file and 1 empty-type file")
      codeBaseAnalyzeAggregator.countFileTypeNum(ls) should contain theSameElementsAs Map[String, Int](("scala", 2), (FileUtil.EmptyFileType, 1), ("sbt", 1))
    }
    scenario("analyze avg file count") {
      Given("a.scala: 10 lines, b.scala: 10 lines, c.sbt: 5 lines, d: 5 lines")
      val ls = List("a.scala", "b.scala", "c.sbt", "d")
      val sourceCodeInfos = List(
        SourceCodeInfo("a.scala", "a.scala", 10),
        SourceCodeInfo("b.scala", "b.scala", 10),
        SourceCodeInfo("c.sbt", "c.sbt", 5),
        SourceCodeInfo("d", "d", 5)
      )
      When("calculte avg file count")
      val avgLines = codeBaseAnalyzeAggregator.avgLines(ls, sourceCodeInfos)
      Then("result should be 7.5")
      avgLines shouldBe 7.5
    }
    scenario("find longest file") {
      Given("a.scala: 10 lines, b.scala: 5 lines")
      When("finding longest file")
      Then("a.scala should be returned")
      codeBaseAnalyzeAggregator.longestFile(List(aInfo, bInfo)) shouldBe aInfo
    }
    scenario("find top 10 longest files") {
      Given("11 files whose line of code is 1 to 11")
      val sourceCodeInfos = for (i <- 1 to 11) yield SourceCodeInfo(s"$i.scala", s"$i.scala", i)
      When("finding top 10 longest files")
      val top10LongFiles = codeBaseAnalyzeAggregator.top10Files(sourceCodeInfos)
      Then("10 files should be returned")
      top10LongFiles should have size 10
      And("result should not contains file whose line of code 1")
      top10LongFiles should not contain SourceCodeInfo("1.scala", "1.scala", 1)
    }
    scenario("count total line numbers") {
      Given("a.scala: 10 lines, b.scala: 5")
      When("count total line numbers")
      Then("total line numbers should be 15")
      codeBaseAnalyzeAggregator.totalLineCount(List(aInfo, bInfo)) shouldBe 15
    }
  }
}