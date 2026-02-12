package topwords

import mainargs.{main, arg, ParserForMethods}
import java.io.IOException
import com.typesafe.scalalogging.LazyLogging

object Topwords extends LazyLogging:
  @main
  def run(
    @arg(name = "cloud-size", short = 'c') cloudSize: Int = 10,
    @arg(name = "length-at-least", short = 'l') minLength: Int = 6,
    @arg(name = "window-size", short = 'w') windowSize: Int = 1000
  ): Unit =
    // Log configuration as per requirement
    logger.debug(s"howMany=$cloudSize minLength=$minLength lastNWords=$windowSize")

    val engine = Wordcloud(cloudSize, minLength, windowSize)
    
    try
      val scanner = java.util.Scanner(System.in)
      while scanner.hasNext do
        val word = scanner.next()
        engine.update(word) match
          case Some(cloud) =>
            val output = cloud.map((w, f) => s"$w: $f").mkString(" ")
            println(output)
            
            // Handle SIGPIPE: Stop gracefully if stdout is closed (e.g., by 'head')
            if System.out.checkError() then throw new IOException("SIGPIPE")
          case None => // Wait for window to fill
    catch
      case _: IOException => System.exit(0)

  def main(args: Array[String]): Unit = 
    ParserForMethods(this).runOrExit(args.toIndexedSeq): Unit