package topwords

import mainargs.{main, arg, ParserForMethods}
import java.io.{InputStream, PrintStream, IOException}
import scala.io.Source

// Inherit the trait to use the pure functions
object Main extends WordCloudLogic:

  @main
  def run(
    @arg(name = "cloud-size", short = 'c') howMany: Int = 10,
    @arg(name = "length-at-least", short = 'l') minLength: Int = 6,
    @arg(name = "window-size", short = 'w') lastNWords: Int = 1000
  ): Unit =
    processStream(howMany, minLength, lastNWords, System.in, System.out)

  def processStream(
    howMany: Int, 
    minLength: Int, 
    lastNWords: Int, 
    input: InputStream, 
    output: PrintStream
  ): Unit =
    // 1. Create a lazy iterator from the input stream
    val wordStream = Source.fromInputStream(input, "UTF-8")
      .getLines()
      .flatMap(_.split("\\s+"))
      .filter(_.nonEmpty)

    // 2. The Core Requirement: Iterator.scanLeft
    // This passes the CloudState along the stream, updating it with each word
    val stateStream = wordStream.scanLeft(CloudState()) { (currentState, word) =>
      update(currentState, word, lastNWords, minLength)
    }

    try
      // 3. Transform states to output strings and print them interactively
      stateStream
        .drop(1) // Skip the initial empty state emitted by scanLeft
        .map(state => format(state, howMany))
        .filter(_.nonEmpty) // Don't print blank lines
        .foreach(line => output.println(line))
        
    catch
      case e: IOException if e.getMessage != null && e.getMessage.contains("Broken pipe") =>
        // Requirement: Handle SIGPIPE as described in the notes
        sys.exit(0)
      case e: Exception =>
        System.err.println(s"Unexpected error: ${e.getMessage}")
        sys.exit(1)

  def main(args: Array[String]): Unit = 
    val _ = ParserForMethods(this).runOrExit(args.toIndexedSeq)