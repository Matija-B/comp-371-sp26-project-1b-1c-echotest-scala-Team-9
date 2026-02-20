package topwords

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.collection.immutable.Queue

// A dummy object to access the trait's functions for testing
object LogicTester extends WordCloudLogic

class LogicSpec extends AnyWordSpec with Matchers {

  "WordCloudLogic" should {

    "add a word to an empty state" in {
      val startState = CloudState()
      val endState = LogicTester.update(startState, "hello", windowSize = 10, minLen = 2)

      endState.queue should contain("hello")
      endState.counts should contain("hello" -> 1)
    }

    "ignore words shorter than minLength" in {
      val startState = CloudState()
      val endState = LogicTester.update(startState, "a", windowSize = 10, minLen = 3)

      // State should not change
      endState.queue should be(empty)
      endState.counts should be(empty)
    }

    "increment count for existing words" in {
      val startState = CloudState(Queue("hello"), Map("hello" -> 1))
      val endState = LogicTester.update(startState, "hello", windowSize = 10, minLen = 2)

      endState.counts("hello") should be(2)
      endState.queue should have size 2
    }

    "evict the oldest word when window is full" in {
      // Setup a full window of size 2: ["one", "two"]
      val startState = CloudState(
        Queue("one", "two"), 
        Map("one" -> 1, "two" -> 1)
      )

      // Add "three". "one" should be pushed out.
      val endState = LogicTester.update(startState, "three", windowSize = 2, minLen = 2)

      // Check Queue (one is gone)
      endState.queue should contain inOrder ("two", "three")
      
      // Check Counts
      endState.counts should contain("two" -> 1)
      endState.counts should contain("three" -> 1)
      endState.counts.get("one") should be(None) 
    }
  }
}