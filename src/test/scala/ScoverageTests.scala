package com.github.plippe

import java.io.File
import utest._

object ScoverageTests extends TestSuite {
    val tests = Tests {

        'testScoverageXmlReaderRead - {
            val file = new File(getClass.getResource("/scoverage.empty.xml").getFile())
            assert(ScoverageXmlReader.read(file).isRight)
        }

        'testScoverageXmlReaderReadBad - {
            val file = new File("doesn't exist")
            assert(ScoverageXmlReader.read(file).isLeft)
        }

        'testNamedCoveragesRender - {
            def coverage(fileName: String) = {
                val file = new File(getClass.getResource(fileName).getFile())
                ScoverageXmlReader.read(file).right.get
            }

            val coverages = Array(
                NamedCoverage("a", coverage("/scoverage.a.xml")),
                NamedCoverage("b", coverage("/scoverage.b.xml")),
                NamedCoverage("c", coverage("/scoverage.empty.xml"))
            )

            assert(NamedCoverages.render(coverages, 50).replace(" ", "_") ==
                """|__________________a___________b___________c_____
                   |--------------------------------------------------
                   |Coverage__________50.00%______66.67%_____100.00%
                   |--------------------------------------------------
                   |Files__________________1___________1___________0
                   |Statements_____________2___________3___________0
                   |--------------------------------------------------
                   |Hits___________________1___________2___________0
                   |Misses_________________1___________1___________0""".stripMargin
            )
        }
    }
}
