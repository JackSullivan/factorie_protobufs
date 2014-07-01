package edu.umass.cs.iesl.factorie_protobufs

import edu.umass.cs.iesl.tackbp2014.load.LoadTac
import cc.factorie.util.FileUtils
import edu.umass.cs.iesl.tackbp2014.process.{SlotFillingProcessingComponents, SlotFillingPreprocessingComponents}
import java.io.{FileWriter, BufferedWriter}

/**
 * @author John Sullivan
 */
object Test {
  val dataDirs = Seq(
    "/iesl/canvas/proj/tackbp2014/data/LDC2014E20_TAC_2014_KBP_Event_Argument_Extraction_Pilot_Source_Corpus_V1.1/data/df",
    "/iesl/canvas/proj/tackbp2014/data/LDC2014E20_TAC_2014_KBP_Event_Argument_Extraction_Pilot_Source_Corpus_V1.1/data/nw")
//    "/Users/johnsullivan/data/tackbp2014/data/LDC2014E20_TAC_2014_KBP_Event_Argument_Extraction_Pilot_Source_Corpus_V1.1/data/df",
//    "/Users/johnsullivan/data/tackbp2014/data/LDC2014E20_TAC_2014_KBP_Event_Argument_Extraction_Pilot_Source_Corpus_V1.1/data/nw")

  //val writeDir = "/Users/johnsullivan/dev/fac-reader/test-out"
  val writeDir = "/iesl/canvas/sullivan/dev/factorie_protobufs/test-out"


  def main(args:Array[String]) {

    val docs = dataDirs.flatMap(FileUtils.getFileListFromDir(_)).flatMap(LoadTac.fromFilename)

    println("Found %d docs".format(docs.size))

    val toProcess = docs.take(1)

    println("processing %d docs".format(toProcess.size))

    val process = SlotFillingPreprocessingComponents.process1 _ andThen SlotFillingProcessingComponents.process1

    toProcess.zipWithIndex.foreach { case (doc, idx) =>
      println("processng doc %d (%s)".format(idx, doc.name))
      process(doc)
      println("Finished processing doc %d (%s)".format(idx, doc.name))
      val ser = doc.serialize
      println("serialized doc %d (%s)".format(idx, doc.name))
      val wrt1 = new BufferedWriter(new FileWriter("%s.readproto".format(doc.name)))
      wrt1.write(ser.toString)
      wrt1.flush()
      wrt1.close()
      ser.writeStructuredTo(writeDir)
      println("successfully wrote doc %d (%s) to file".format(idx, doc.name))
      val deSer = ProtoDocument.readById(writeDir, doc.name)
      println("sucessfully read %d (%s) from file (is defined: %s)".format(idx, doc.name, deSer.isDefined))

      deSer.foreach { itm =>
        val doc = itm.deserialize
        println("deserialized doc %d (%s)".format(idx, doc.name))
      }
    }

  }
}
