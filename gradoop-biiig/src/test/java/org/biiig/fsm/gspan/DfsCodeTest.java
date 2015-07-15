package org.biiig.fsm.gspan;

import org.biiig.model.LabeledEdge;
import org.biiig.model.LabeledVertex;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class DfsCodeTest {

  @Test
  public void testComparison(){
    /*
    LabeledVertex vA = new LabeledVertex("A");
    LabeledVertex vB = new LabeledVertex("B");
    LabeledVertex vC = new LabeledVertex("C");

    DfsCode vACode = new DfsCode();
    vACode.add(vA);

    DfsCode vBCode = new DfsCode();
    vBCode.add(vB);

    // (A) < (B)
    assertThat(vACode.compareTo(vBCode), Is.is(-1));

    vACode.add(vB);
    vACode.growBy(new LabeledEdge(vA, "a", vB));

    System.out.println(vACode);
    System.out.println(vBCode);

    // (A)-a->(B) < (B)
    assertThat(vACode.compareTo(vBCode), Is.is(-1));

    vACode.add(vC);
    vACode.growBy(new LabeledEdge(vB, "b", vC));

    vBCode.add(vC);
    vBCode.growBy(new LabeledEdge(vB, "b", vC));
    vBCode.add(vA);
    vBCode.growBy(new LabeledEdge(vA, "a", vB));

    // (A)-a->(B),(B)-b->(C) < (B)-b->(C),(A)-a->(B)
    assertThat(vACode.compareTo(vBCode), Is.is(-1));


    */
  }



}