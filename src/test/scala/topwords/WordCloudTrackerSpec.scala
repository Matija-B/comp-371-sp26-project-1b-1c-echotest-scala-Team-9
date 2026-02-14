package topwords

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class WordcloudtrackerSpec extends AnyWordSpec with Matchers {

  "Wordcloudtracker" should {
    
    // --- Basic Logic Tests ---
    "return None until the window is full" in {
      val tracker = Wordcloudtracker(howMany = 3, minLength = 2, windowSize = 3)
      tracker.addWord("aa") shouldBe None
      tracker.addWord("bb") shouldBe None
      tracker.addWord("cc").isDefined shouldBe true
    }

    "filter out words shorter than minLength" in {
      val tracker = Wordcloudtracker(howMany = 3, minLength = 5, windowSize = 2)
      tracker.addWord("a") shouldBe None // Ignored
      tracker.addWord("apple") shouldBe None // Window at 1
      tracker.addWord("b") shouldBe None // Ignored
      tracker.addWord("banana").isDefined shouldBe true // Window at 2
    }

    "sort by frequency then alphabetically" in {
      val tracker = Wordcloudtracker(howMany = 3, minLength = 1, windowSize = 4)
      tracker.addWord("zz")
      tracker.addWord("aa")
      tracker.addWord("zz")
      // Ties: 'aa' (1) and 'zz' (2). Wait, let's make them equal freq.
      // add 'aa' again -> aa: 2, zz: 2.
      // Alphabetical means 'aa' comes first.
      val result = tracker.addWord("aa") 
      
      result shouldBe Some("aa: 2 zz: 2")
    }

    // --- New Tests for Higher Coverage ---

    "properly evict the oldest word and remove it from counts" in {
      val tracker = Wordcloudtracker(howMany = 10, minLength = 1, windowSize = 2)
      tracker.addWord("apple")
      tracker.addWord("banana") // Window full: apple: 1 banana: 1
      val result = tracker.addWord("cherry") 
      
      // "apple" (the oldest) should be evicted. 
      // Result should only contain banana and cherry.
      result.get should not include ("apple")
      result.get should include ("banana: 1")
      result.get should include ("cherry: 1")
    }

    "handle a cloud size larger than the number of unique words" in {
      val tracker = Wordcloudtracker(howMany = 100, minLength = 1, windowSize = 3)
      tracker.addWord("a")
      tracker.addWord("a")
      val result = tracker.addWord("a")
      
      // Should not crash trying to take 100 words when only 1 exists
      result.get shouldBe "a: 3" 
    }

    "ignore empty strings or purely whitespace if passed" in {
      val tracker = Wordcloudtracker(howMany = 5, minLength = 3, windowSize = 1)
      tracker.addWord("") shouldBe None
      tracker.addWord("  ") shouldBe None
      // 'ab' is length 2, minLength is 3 -> ignored
      tracker.addWord("ab") shouldBe None
      
      // 'abc' is length 3 -> accepted -> window full -> output
      tracker.addWord("abc").isDefined shouldBe true
    }
    "decrease count but keep word in map if it appears multiple times in window" in {
      val tracker = Wordcloudtracker(howMany = 10, minLength = 1, windowSize = 2)
      tracker.addWord("apple")
      tracker.addWord("apple") // Window: [apple, apple], Count: apple->2
      
      val result = tracker.addWord("banana") 
      // Window: [apple, banana]. 
      // One "apple" evicted, but count is still 1. Map should NOT remove "apple".
      
      result.get should include ("apple: 1")
      result.get should include ("banana: 1")
    }
  }
}