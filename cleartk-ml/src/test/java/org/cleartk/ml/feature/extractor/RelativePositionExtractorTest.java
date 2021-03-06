/** 
 * Copyright (c) 2007-2008, Regents of the University of Colorado 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * Neither the name of the University of Colorado at Boulder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. 
 */
package org.cleartk.ml.feature.extractor;

import java.util.List;

import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.extractor.RelativePositionExtractor;
import org.cleartk.test.util.DefaultTestBase;
import org.junit.Assert;
import org.junit.Test;

/**
 * <br>
 * Copyright (c) 2007-2008, Regents of the University of Colorado <br>
 * All rights reserved.
 * 
 * 
 * @author Steven Bethard
 */

public class RelativePositionExtractorTest extends DefaultTestBase {

  @Test
  public void testEquals() {
    this.testOne(5, 8, 5, 8, "EQUALS");
  }

  @Test
  public void testContains() {
    this.testOne(5, 8, 6, 7, "CONTAINS");
    this.testOne(5, 8, 5, 7, "CONTAINS");
    this.testOne(5, 8, 6, 8, "CONTAINS");
  }

  @Test
  public void testContainedBy() {
    this.testOne(5, 8, 3, 8, "CONTAINEDBY");
  }

  @Test
  public void testOverlapsLeft() {
    this.testOne(0, 3, 1, 4, "OVERLAPS_LEFT");
    this.testOne(0, 2, 1, 4, "OVERLAPS_LEFT");
  }

  @Test
  public void testOverlapsRight() {
    this.testOne(19, 21, 10, 20, "OVERLAPS_RIGHT");
    this.testOne(15, 25, 10, 20, "OVERLAPS_RIGHT");
  }

  @Test
  public void testLeftOf() {
    this.testOne(1, 3, 4, 6, "LEFTOF");
    this.testOne(2, 4, 4, 6, "LEFTOF");
  }

  @Test
  public void testRightOf() {
    this.testOne(6, 10, 4, 6, "RIGHTOF");
    this.testOne(6, 10, 2, 5, "RIGHTOF");
  }

  private void testOne(int begin1, int end1, int begin2, int end2, String expected) {
    Annotation annotation1 = new Annotation(jCas, begin1, end1);
    Annotation annotation2 = new Annotation(jCas, begin2, end2);
    RelativePositionExtractor<Annotation, Annotation> extractor;
    List<Feature> features;

    extractor = new RelativePositionExtractor<Annotation, Annotation>();
    features = extractor.extract(jCas, annotation1, annotation2);
    Assert.assertEquals(1, features.size());
    Assert.assertEquals("RelativePosition", features.get(0).getName());
    Assert.assertEquals(expected, features.get(0).getValue());

    extractor = new RelativePositionExtractor<Annotation, Annotation>();
    features = extractor.extract(jCas, annotation1, annotation2);
    Assert.assertEquals(1, features.size());
    Assert.assertEquals("RelativePosition", features.get(0).getName());
    Assert.assertEquals(expected, features.get(0).getValue());
  }

}
