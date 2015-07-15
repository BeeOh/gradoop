package org.biiig.model;


import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class LabeledEdgeTest {
  @Test
  public void testComparator(){

    LabeledVertex v1 = new LabeledVertex("A");
    LabeledVertex v2 = new LabeledVertex("B");
    LabeledVertex v3 = new LabeledVertex("C");

    // (A)-c->(B)
    LabeledEdge e1 = new LabeledEdge(v1,"c",v2);
    // (A)-c->(B)
    LabeledEdge e2 = new LabeledEdge(v1,"c",v2);
    // (B)-c->(B)
    LabeledEdge e3 = new LabeledEdge(v2,"c",v2);
    // (B)-d->(B)
    LabeledEdge e4 = new LabeledEdge(v2,"d",v2);
    // (B)-c->(C)
    LabeledEdge e5 = new LabeledEdge(v2,"c",v3);
    // (A)<-c-(B)
    LabeledEdge e6 = new LabeledEdge(v2,"c",v1);

    // equivalence
    // (A)-c->(B) = (A)-c->(B)
    assertThat(e1.compareTo(e2), Is.is(0));
    // min min vertex label
    // (A)-c->(B) < (B)-c->(B)
    assertThat(e1.compareTo(e3), Is.is(-1));
    // min edge label
    // (B)-c->(B) < (B)-d->(B)
    assertThat(e3.compareTo(e4), Is.is(-1));
    // min max vertex label
    // (B)-c->(B) < (B)-c->(C)
    assertThat(e3.compareTo(e5), Is.is(-1));
    // min-to->max
    // (A)-c->(B) < (A)<-c-(B)
    assertThat(e1.compareTo(e6), Is.is(-1));

  }

}