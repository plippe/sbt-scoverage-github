package com.github.plippe

import utest._

object TableTests extends TestSuite {
    val tests = Tests {

        'testCellEmpty - {
            assert(Cell.empty.render(-1) == "")
            assert(Cell.empty.render(0) == "")
            assert(Cell.empty.render(1) == " ")
            assert(Cell.empty.render(3) == "   ")
            assert(Cell.empty.render(5) == "     ")
        }

        'testCellFilled - {
            assert(Cell.filled("#").render(-1) == "")
            assert(Cell.filled("#").render(0) == "")
            assert(Cell.filled("#").render(1) == "#")
            assert(Cell.filled("-").render(3) == "---")
            assert(Cell.filled("-_").render(5) == "-_-_-")
        }

        'testCellAlignLeft - {
            assert(Cell.alignLeft("abc").render(-1) == "")
            assert(Cell.alignLeft("abc").render(0) == "")
            assert(Cell.alignLeft("abc").render(1) == "a")
            assert(Cell.alignLeft("abc").render(3) == "abc")
            assert(Cell.alignLeft("abc").render(5) == "abc  ")
        }

        'testCellAlignRight - {
            assert(Cell.alignRight("abc").render(-1) == "")
            assert(Cell.alignRight("abc").render(0) == "")
            assert(Cell.alignRight("abc").render(1) == "c")
            assert(Cell.alignRight("abc").render(3) == "abc")
            assert(Cell.alignRight("abc").render(5) == "  abc")
        }

        'testCellAlignMiddle - {
            assert(Cell.alignMiddle("abc").render(-1) == "")
            assert(Cell.alignMiddle("abc").render(0) == "")
            assert(Cell.alignMiddle("abc").render(1) == "a")
            assert(Cell.alignMiddle("abc").render(3) == "abc")
            assert(Cell.alignMiddle("abc").render(5) == " abc ")
            assert(Cell.alignMiddle("abc").render(6) == "  abc ")
            assert(Cell.alignMiddle("abc").render(7) == "  abc  ")
        }

        'testRowEmpty - {
            assert(Row.empty.render(-1) == "")
            assert(Row.empty.render(0) == "")
            assert(Row.empty.render(1) == " ")
            assert(Row.empty.render(3) == "   ")
            assert(Row.empty.render(5) == "     ")
        }

        'testRowFilled - {
            assert(Row.filled("#").render(-1) == "")
            assert(Row.filled("#").render(0) == "")
            assert(Row.filled("#").render(1) == "#")
            assert(Row.filled("-").render(3) == "---")
            assert(Row.filled("-_").render(5) == "-_-_-")
        }

        'testRowWithCells - {
            val cells = Array(Cell.filled("a"), Cell.filled("b"), Cell.filled("c"))
            assert(Row.withCells(cells).render(-1) == "")
            assert(Row.withCells(cells).render(0) == "")
            assert(Row.withCells(cells).render(1) == "")
            assert(Row.withCells(cells).render(3) == "abc")
            assert(Row.withCells(cells).render(5) == "abc")
            assert(Row.withCells(cells).render(6) == "aabbcc")
            assert(Row.withCells(cells).render(7) == "aabbcc")
        }

        'testRowWithCellsEmpty - {
            val cells = Array.empty[Cell]
            assert(Row.withCells(cells).render(-1) == "")
            assert(Row.withCells(cells).render(0) == "")
            assert(Row.withCells(cells).render(1) == " ")
            assert(Row.withCells(cells).render(3) == "   ")
        }

        'testTable - {
            val rows = Array(Row.filled("a"), Row.filled("b"), Row.filled("c"))
            assert(Table(rows).render(-1) == "\n\n")
            assert(Table(rows).render(0) == "\n\n")
            assert(Table(rows).render(1) == "a\nb\nc")
            assert(Table(rows).render(3) == "aaa\nbbb\nccc")
            assert(Table(rows).render(5) == "aaaaa\nbbbbb\nccccc")
        }

        'testTableEmpty - {
            val rows = Array.empty[Row]
            assert(Table(rows).render(-1) == "")
            assert(Table(rows).render(0) == "")
            assert(Table(rows).render(1) == " ")
            assert(Table(rows).render(3) == "   ")
        }

    }
}
