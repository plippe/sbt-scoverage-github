package com.github.plippe

case class Table(rows: Array[Row]) {
    def render(width: Int): String = {
        val nonEmptyRows = if(rows.nonEmpty) rows else Array(Row.empty)
        nonEmptyRows.map(_.render(width)).mkString("\n")
    }
}

trait Row {
    def render(width: Int): String
}

object Row {
    def empty() = new Row { def render(width: Int) = " " * width }
    def filled(value: String) = new Row { def render(width: Int) = (value * width).take(width) }

    def withCells(cells: Array[Cell]) = {
        new Row {
            def render(width: Int): String = {
                val nonEmptyCells = if(cells.nonEmpty) cells else Array(Cell.empty)
                val cellWidth = width / nonEmptyCells.length
                nonEmptyCells.map(_.render(cellWidth)).mkString
            }
        }
    }
}

trait Cell {
    def value: String
    def render(width: Int): String
}

object Cell {

    def apply(v: String, r: (Int) => String): Cell = new Cell {
        def value: String = v
        def render(width: Int): String = r(width)
    }

    def empty(): Cell = Cell.apply(" ", { w => " " * w })
    def filled(value: String): Cell =
        Cell.apply(value, { w => (value * w).take(w) })

    def alignLeft(value: String): Cell =
        Cell.apply(value, { w => (value + " " * w).take(w) })

    def alignRight(value: String): Cell =
        Cell.apply(value, { w => (" " * w + value).takeRight(w) })

    def alignMiddle(value: String): Cell = {
        def render(width: Int) = {
            val paddingHalfWidth = ((width - value.length) / 2f).ceil.toInt
            val paddedValue = " " * paddingHalfWidth + value + " " * paddingHalfWidth
            paddedValue.take(width)
        }

        Cell.apply(value, render)
    }

}
