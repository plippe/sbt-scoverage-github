package com.github.plippe

import java.io.File
import scala.util.Try
import scoverage.Coverage
import scoverage.report.{ ScoverageXmlReader => OfficialScoverageXmlReader }

import com.github.plippe.implicits._
trait Scala212_Scoverage { avoidUnusedImport() }

object ScoverageXmlReader {
    def read(file: File): Either[Throwable, Coverage] = Try(OfficialScoverageXmlReader.read(file)).toEither
}

case class NamedCoverage(name: String, coverage: Coverage)

object NamedCoverages {
    def render(namedCoverages: Array[NamedCoverage], width: Int): String = {
        val table = Table(
            Array(
                Row.withCells(Cell.empty +: namedCoverages.map { c => Cell.alignMiddle(c.name) }),
                Row.filled("-"),
                Row.withCells(Cell.alignLeft("Coverage") +: namedCoverages.map { c => Cell.alignRight(c.coverage.statementCoverageFormatted.toString + "%") }),
                Row.filled("-"),
                Row.withCells(Cell.alignLeft("Files") +: namedCoverages.map { c => Cell.alignRight(c.coverage.fileCount.toString) }),
                Row.withCells(Cell.alignLeft("Statements") +: namedCoverages.map { c => Cell.alignRight(c.coverage.statementCount.toString) }),
                Row.filled("-"),
                Row.withCells(Cell.alignLeft("Hits") +: namedCoverages.map { c => Cell.alignRight(c.coverage.invokedStatementCount.toString) }),
                Row.withCells(Cell.alignLeft("Misses") +: namedCoverages.map { c => Cell.alignRight((c.coverage.statementCount - c.coverage.invokedStatementCount).toString) })
            )
        )

        table.render(width)
    }
}
