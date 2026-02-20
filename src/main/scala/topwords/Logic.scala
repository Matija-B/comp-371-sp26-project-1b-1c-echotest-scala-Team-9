package topwords

import scala.collection.immutable.Queue

// The immutable "State" of your system
case class CloudState(
  queue: Queue[String] = Queue.empty, // The FIFO window
  counts: Map[String, Int] = Map.empty // The frequency map
)

// The Trait (Requirement: "use Scala traits for modularity")
trait WordCloudLogic:

  // Pure function: (OldState, Word) => NewState
  def update(state: CloudState, word: String, windowSize: Int, minLen: Int): CloudState =
    if word.length < minLen then 
      state // Ignore short words, state is unchanged
    else
      // 1. Add the new word
      val q1 = state.queue.enqueue(word)
      val c1 = state.counts.updated(word, state.counts.getOrElse(word, 0) + 1)

      // 2. Check if we need to evict the oldest word
      if q1.size <= windowSize then
        CloudState(q1, c1)
      else
        // 3. Evict and update counts
        val (removedWord, q2) = q1.dequeue
        val currentCount = c1(removedWord)
        
        val c2 = if currentCount == 1 then 
          c1 - removedWord 
        else 
          c1.updated(removedWord, currentCount - 1)
          
        CloudState(q2, c2)

  // Pure function to format the state into the required output string
  def format(state: CloudState, limit: Int): String =
    state.counts.toSeq
      .sortBy { case (w, c) => (-c, w) } // Sort by count descending, then alphabetically
      .take(limit)
      .map { case (w, c) => s"$w: $c" }
      .mkString(" ")