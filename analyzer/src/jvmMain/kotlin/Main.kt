import com.gabrielleeg1.plank.analyzer.ModuleTree
import com.gabrielleeg1.plank.analyzer.analyze
import com.gabrielleeg1.plank.grammar.debug.dumpTree
import com.gabrielleeg1.plank.grammar.element.PlankFile

fun main() {
  val file = PlankFile.of(
    """
    fun main(argc: Int, argv: **Char): Void {
      println("Hello, world");
    }
    """.trimIndent(),
  )

  val resolved = analyze(file, ModuleTree())

  println(resolved.dumpTree())
}
