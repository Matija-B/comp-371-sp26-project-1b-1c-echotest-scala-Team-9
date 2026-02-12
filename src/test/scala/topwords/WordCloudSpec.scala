package topwords

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WordCloudSpec extends AnyFlatSpec with Matchers:

  "WordCloud" should "ignore words shorter than minLength" in {
    val engine = WordCloud(cloudSize = 3, minLength = 5, windowSize = 2)
    engine.update("tiny") should be(None) // "tiny" is length 4
    engine.update("large") should be(None) // Only 1 valid word so far
  }

  it should "not produce output until the window is full" in {
    val engine = WordCloud(cloudSize = 2, minLength = 2, windowSize = 3)
    engine.update("aa") should be(None)
    engine.update("bb") should be(None)
    val result = engine.update("cc")
    result.isDefined should be(true)
  }

  it should "handle the example case from the requirements" in {
    // Setup: -c 3 -l 2 -w 5
    val engine = WordCloud(cloudSize = 3, minLength = 2, windowSize = 5)
    
    // Sequence: a b c aa bb cc (Note: 'a','b','c' are length 1, ignored if minLength=2)
    // Actually, following the example: a, b, c (len 1) are ignored. 
    // aa, bb, cc, aa, bb -> window is full
    engine.update("aa")
    engine.update("bb")
    engine.update("cc")
    engine.update("aa")
    val result = engine.update("bb")
    
    val expected = Seq(("aa", 2), ("bb", 2), ("cc", 1))
    result.get should contain theSameElementsInOrderAs expected
  }

  it should "sort alphabetically when frequencies are tied" in {
    val engine = WordCloud(cloudSize = 2, minLength = 2, windowSize = 2)
    engine.update("zz")
    val result = engine.update("aa") // Both have frequency 1
    
    // 'aa' should come before 'zz' alphabetically
    result.get.head._1 should be("aa")
  }