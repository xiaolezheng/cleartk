/*
 * Copyright (c) 2011, Regents of the University of Colorado 
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
package org.cleartk.timeml.tlink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;
import org.cleartk.ml.feature.extractor.TypePathExtractor;
import org.cleartk.ml.liblinear.LibLinearStringOutcomeDataWriter;
import org.cleartk.syntax.constituent.type.TreebankNode;
import org.cleartk.syntax.constituent.type.TreebankNodeUtil;
import org.cleartk.timeml.type.Event;
import org.cleartk.timeml.util.CleartkInternalModelFactory;
import org.cleartk.token.type.Sentence;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;

import com.google.common.collect.Lists;

/**
 * <br>
 * Copyright (c) 2011, Regents of the University of Colorado <br>
 * All rights reserved.
 * 
 * @author Steven Bethard
 */
public class TemporalLinkMainEventToNextSentenceMainEventAnnotator extends
    TemporalLinkAnnotator_ImplBase<Event, Event> {

  public static final CleartkInternalModelFactory FACTORY = new CleartkInternalModelFactory() {
    @Override
    public Class<?> getAnnotatorClass() {
      return TemporalLinkMainEventToNextSentenceMainEventAnnotator.class;
    }

    @Override
    public Class<?> getDataWriterClass() {
      return LibLinearStringOutcomeDataWriter.class;
    }

    @Override
    public AnalysisEngineDescription getBaseDescription() throws ResourceInitializationException {
      return AnalysisEngineFactory.createEngineDescription(TemporalLinkMainEventToNextSentenceMainEventAnnotator.class);
    }
  };

  public TemporalLinkMainEventToNextSentenceMainEventAnnotator() {
    super(Event.class, Event.class, "BEFORE", "OVERLAP", "AFTER");
  }

  @Override
  public void initialize(UimaContext context) throws ResourceInitializationException {
    super.initialize(context);

    List<FeatureExtractor1<Event>> extractors = Lists.newArrayList();
    extractors.add(new TypePathExtractor<Event>(Event.class, "tense"));
    extractors.add(new TypePathExtractor<Event>(Event.class, "aspect"));
    extractors.add(new TypePathExtractor<Event>(Event.class, "eventClass"));
    
    this.setSourceExtractors(extractors);
    this.setTargetExtractors(extractors);
  }

  @Override
  protected List<SourceTargetPair> getSourceTargetPairs(JCas jCas) {
    List<SourceTargetPair> pairs = Lists.newArrayList();
    Iterator<Sentence> sentences = JCasUtil.select(jCas, Sentence.class).iterator();
    Sentence prev = sentences.hasNext() ? sentences.next() : null;
    while (sentences.hasNext()) {
      Sentence curr = sentences.next();
      Event source = getMainEvent(jCas, prev);
      Event target = getMainEvent(jCas, curr);
      if (source != null && target != null) {
        pairs.add(new SourceTargetPair(source, target));
      }
      prev = curr;
    }
    return pairs;
  }

  private Event getMainEvent(JCas jCas, Sentence sentence) {
    // group events by their depth, and record the minimum depth
    Integer minDepth = null;
    Map<Integer, List<Event>> depthEvents = new HashMap<Integer, List<Event>>();
    for (Event event : JCasUtil.selectCovered(jCas, Event.class, sentence)) {
      TreebankNode node = TreebankNodeUtil.selectMatchingLeaf(jCas, event);
      Integer depth = node == null ? null : TreebankNodeUtil.getDepth(node);
      if (!depthEvents.containsKey(depth)) {
        depthEvents.put(depth, new ArrayList<Event>());
      }
      depthEvents.get(depth).add(event);
      if (depth != null && (minDepth == null || depth < minDepth)) {
        minDepth = depth;
      }
    }
    // select the last event
    if (depthEvents.isEmpty()) {
      return null;
    } else {
      List<Event> events = depthEvents.get(minDepth);
      return events.get(events.size() - 1);
    }
  }
}
