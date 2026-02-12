package topwords

import scala.collection.mutable

class Wordcloud(cloudSize: Int, minLength: Int, windowSize: Int):
  private val window = mutable.Queue[String]()
  private val counts = mutable.Map[String, Int]().withDefaultValue(0)

  /**
   * Processes a single word. Returns a list of (word, frequency) if the 
   * window is full, otherwise returns None.
   */
  def update(word: String): Option[Seq[(String, Int)]] =
    if word.length < minLength then return None

    // Add new word to window and update frequency
    window.enqueue(word)
    counts(word) += 1

    // Slide the window: remove the oldest word if we exceed windowSize
    if window.size > windowSize then
      val removed = window.dequeue()
      counts(removed) -= 1
      // Use 'val _' to suppress the "discarded non-Unit value" warning
      if counts(removed) <= 0 then counts.remove(removed)

    // Return the cloud only when the window is fully populated
    if window.size >= windowSize then
      Some(
        counts.toSeq
          .sortBy((w, f) => (-f, w)) // Sort by frequency (desc), then word (asc)
          .take(cloudSize)
      )
    else
      None