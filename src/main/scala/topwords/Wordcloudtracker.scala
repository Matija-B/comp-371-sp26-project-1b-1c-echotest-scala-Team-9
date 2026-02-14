package topwords

import scala.collection.mutable

class Wordcloudtracker(howMany: Int, minLength: Int, windowSize: Int):
  private val queue = mutable.Queue[String]()
  private val counts = mutable.Map[String, Int]().withDefaultValue(0)

  def addWord(word: String): Option[String] =
    if word.length >= minLength then
      // Add new word
      counts(word) += 1
      queue.enqueue(word)

      // Evict old word if window is full
      if queue.size > windowSize then
        val removed = queue.dequeue()
        counts(removed) -= 1
        if counts(removed) <= 0 then counts.remove(removed)

      // Only return output if we have a full window
      if queue.size == windowSize then
        Some(generateCloudString())
      else None
    else None

  private def generateCloudString(): String =
    counts.toSeq
      .sortBy { case (word, freq) => (-freq, word) } // Descending freq, then alphabetical
      .take(howMany)
      .map { case (word, freq) => s"$word: $freq" }
      .mkString(" ")