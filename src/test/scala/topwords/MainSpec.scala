package topwords

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}
import java.nio.charset.StandardCharsets

class MainSpec extends AnyWordSpec with Matchers {

  "Main" should {
    
    // Test 1: The Helper Method (Safe logic test)
    "processStream correctly handling logic" in {
      val inputString = "a b c aa bb cc aa bb aa bb aa" 
      val inputStream = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8))
      val outputStream = new ByteArrayOutputStream()

      // Call the helper directly
      Main.processStream(3, 2, 5, inputStream, new PrintStream(outputStream))

      val output = outputStream.toString("UTF-8").trim
      val lines = output.split(System.lineSeparator())
      
      lines.last should include("aa: 3")
    }

    // Test 2: The Actual Entry Point (Hijacks System.in/out)
    "execute the full @main run method with defaults" in {
      val input = "test word cloud test word"
      val inStream = new ByteArrayInputStream(input.getBytes)
      val outStream = new ByteArrayOutputStream()
      
      // Save original streams
      val originalIn = System.in
      val originalOut = System.out
      
      try {
        // Hijack streams
        System.setIn(inStream)
        System.setOut(new PrintStream(outStream))
        
        // CALL THE REAL MAIN METHOD
        // This covers the logging and the 'run' method body
        Main.run(howMany = 5, minLength = 1, lastNWords = 5)
        
      } finally {
        // Restore streams (Critical!)
        System.setIn(originalIn)
        System.setOut(originalOut)
      }
      
      val output = outStream.toString.trim
      output should include ("test: 2")
    }
  }
}