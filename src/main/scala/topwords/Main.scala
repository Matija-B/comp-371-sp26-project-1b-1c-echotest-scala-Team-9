package topwords

import mainargs.{main, arg, ParserForMethods}
import com.typesafe.scalalogging.LazyLogging
import java.io.{InputStream, PrintStream}
import java.util.Scanner

object Main extends LazyLogging:

  @main
  def run(
    @arg(name = "cloud-size", short = 'c') howMany: Int = 10,
    @arg(name = "length-at-least", short = 'l') minLength: Int = 6,
    @arg(name = "window-size", short = 'w') lastNWords: Int = 1000
  ): Unit =
    logger.debug(s"howMany=$howMany minLength=$minLength lastNWords=$lastNWords")
    // Pass 'true' here because this is the real application
    processStream(howMany, minLength, lastNWords, System.in, System.out, shouldExitOnFail = true)

  // Add 'shouldExitOnFail' with a default of 'false' for tests
  def processStream(
    howMany: Int, 
    minLength: Int, 
    lastNWords: Int, 
    input: InputStream, 
    output: PrintStream, 
    shouldExitOnFail: Boolean = false
  ): Unit =
    val tracker = Wordcloudtracker(howMany, minLength, lastNWords)
    val scanner = new Scanner(input)
    
    try
      while scanner.hasNext() do
        val word = scanner.next()
        tracker.addWord(word).foreach(output.println)
    catch
      case e: java.io.IOException if e.getMessage != null && e.getMessage.contains("Broken pipe") =>
        sys.exit(0)
      case e: Exception =>
        logger.error("Unexpected error", e)
        // logic is now explicit: no Type Mismatch error!
        if shouldExitOnFail then sys.exit(1) else throw e

  def main(args: Array[String]): Unit = 
    val _ = ParserForMethods(this).runOrExit(args.toIndexedSeq)
