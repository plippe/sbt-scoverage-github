package com.github.plippe

import cats.data.NonEmptyList
import java.io.File
import scala.util.Try
import scoverage.Coverage
import scoverage.report.{ ScoverageXmlReader => OfficialScoverageXmlReader }

object ScoverageXmlReader {
    def read(file: File): Either[Throwable, Coverage] = Try(OfficialScoverageXmlReader.read(file)).toEither
}

case class NamedCoverage(name: String, coverage: Coverage)

object NamedCoverages {
    def render(namedCoverages: NonEmptyList[NamedCoverage], width: Int): String = {
        val table = Table(
            Array(
                Row.withCells(Cell.empty +: namedCoverages.map { c => Cell.alignMiddle(c.name) }.toList.toArray),
                Row.filled("-"),
                Row.withCells(Cell.alignLeft("Coverage") +: namedCoverages.map { c => Cell.alignRight(c.coverage.statementCoverageFormatted.toString + "%") }.toList.toArray),
                Row.filled("-"),
                Row.withCells(Cell.alignLeft("Files") +: namedCoverages.map { c => Cell.alignRight(c.coverage.fileCount.toString) }.toList.toArray),
                Row.withCells(Cell.alignLeft("Statements") +: namedCoverages.map { c => Cell.alignRight(c.coverage.statementCount.toString) }.toList.toArray),
                Row.filled("-"),
                Row.withCells(Cell.alignLeft("Hits") +: namedCoverages.map { c => Cell.alignRight(c.coverage.invokedStatementCount.toString) }.toList.toArray),
                Row.withCells(Cell.alignLeft("Misses") +: namedCoverages.map { c => Cell.alignRight((c.coverage.statementCount - c.coverage.invokedStatementCount).toString) }.toList.toArray)
            )
        )

        table.render(width)
    }
}
