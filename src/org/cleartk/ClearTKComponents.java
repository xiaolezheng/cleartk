/** 
 * Copyright (c) 2009, Regents of the University of Colorado 
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
package org.cleartk;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypePriorities;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.cleartk.classifier.AnnotationHandler;
import org.cleartk.classifier.ClassifierAnnotator;
import org.cleartk.classifier.DataWriterAnnotator;
import org.cleartk.classifier.DataWriterFactory;
import org.cleartk.classifier.InstanceConsumer;
import org.cleartk.classifier.SequentialAnnotationHandler;
import org.cleartk.classifier.SequentialClassifierAnnotator;
import org.cleartk.classifier.SequentialDataWriterAnnotator;
import org.cleartk.classifier.SequentialDataWriterFactory;
import org.cleartk.classifier.SequentialInstanceConsumer;
import org.cleartk.classifier.feature.extractor.outcome.DefaultOutcomeFeatureExtractor;
import org.cleartk.classifier.viterbi.ViterbiDataWriter;
import org.cleartk.classifier.viterbi.ViterbiDataWriterFactory;
import org.cleartk.corpus.timeml.PlainTextTLINKGoldAnnotator;
import org.cleartk.corpus.timeml.TimeMLGoldAnnotator;
import org.cleartk.corpus.timeml.TimeMLWriter;
import org.cleartk.corpus.timeml.TreebankAligningAnnotator;
import org.cleartk.sentence.opennlp.OpenNLPSentenceSegmenter;
import org.cleartk.srl.conll2005.Conll2005GoldAnnotator;
import org.cleartk.srl.conll2005.Conll2005GoldReader;
import org.cleartk.syntax.opennlp.OpenNLPTreebankParser;
import org.cleartk.syntax.treebank.TreebankGoldAnnotator;
import org.cleartk.token.TokenAnnotator;
import org.cleartk.token.opennlp.OpenNLPPOSTagger;
import org.cleartk.token.snowball.SnowballStemmer;
import org.cleartk.util.FilesCollectionReader;
import org.uutuc.factory.AnalysisEngineFactory;
import org.uutuc.factory.CollectionReaderFactory;
import org.uutuc.factory.TypeSystemDescriptionFactory;

/**
 * <br>
 * Copyright (c) 2009, Regents of the University of Colorado <br>
 * All rights reserved.
 * @author Steven Bethard
 */
public class ClearTKComponents {
	
	public static TypeSystemDescription TYPE_SYSTEM_DESCRIPTION =
		TypeSystemDescriptionFactory.createTypeSystemDescription("org.cleartk.TypeSystem");
	
	public static TypePriorities TYPE_PRIORITIES = null;
	
	public static CollectionReader createFilesCollectionReader(String fileOrDir)
	throws ResourceInitializationException {
		return CollectionReaderFactory.createCollectionReader(
				FilesCollectionReader.class,
				ClearTKComponents.TYPE_SYSTEM_DESCRIPTION,
				FilesCollectionReader.PARAM_FILE_OR_DIRECTORY, fileOrDir);
	}
	
	public static CollectionReader createFilesCollectionReaderWithPatterns(
			String dir, String viewName, String ... patterns)
	throws ResourceInitializationException {
		return CollectionReaderFactory.createCollectionReader(
				FilesCollectionReader.class, TYPE_SYSTEM_DESCRIPTION,
				FilesCollectionReader.PARAM_FILE_OR_DIRECTORY, dir,
				FilesCollectionReader.PARAM_VIEW_NAME, viewName,
				FilesCollectionReader.PARAM_PATTERNS, patterns);
	}
	
	public static AnalysisEngineDescription createOpenNLPSentenceSegmenter()
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveAnalysisEngineDescription(
				OpenNLPSentenceSegmenter.class, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES,
				OpenNLPSentenceSegmenter.PARAM_SENTENCE_MODEL_FILE,
				getParameterValue(
						OpenNLPSentenceSegmenter.PARAM_SENTENCE_MODEL_FILE,
						"resources/models/OpenNLP.Sentence.English.bin.gz"));
	}
	
	public static AnalysisEngineDescription createTokenAnnotator()
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveAnalysisEngineDescription(
				TokenAnnotator.class, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES);

	}
	
	public static AnalysisEngineDescription createSnowballStemmer(String stemmerName)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveAnalysisEngineDescription(
				SnowballStemmer.class, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES,
				SnowballStemmer.PARAM_STEMMER_NAME, stemmerName);
	}
	
	public static AnalysisEngineDescription createOpenNLPPOSTagger()
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveAnalysisEngineDescription(
				OpenNLPPOSTagger.class, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES,
				OpenNLPPOSTagger.PARAM_POSTAG_DICTIONARY_FILE,
				getParameterValue(
						OpenNLPPOSTagger.PARAM_POSTAG_DICTIONARY_FILE,
						"resources/models/OpenNLP.TagDict.txt"),
				OpenNLPPOSTagger.PARAM_POSTAG_MODEL_FILE,
				getParameterValue(
						OpenNLPPOSTagger.PARAM_POSTAG_MODEL_FILE,
						"resources/models/OpenNLP.POSTags.English.bin.gz"));
	}
	
	public static AnalysisEngineDescription createOpenNLPTreebankParser()
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveAnalysisEngineDescription(
				OpenNLPTreebankParser.class, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES,
				OpenNLPTreebankParser.PARAM_BUILD_MODEL_FILE,
				getParameterValue(
						OpenNLPTreebankParser.PARAM_BUILD_MODEL_FILE,
						"resources/models/OpenNLP.Parser.English.Build.bin.gz"),
				OpenNLPTreebankParser.PARAM_CHECK_MODEL_FILE,
				getParameterValue(
						OpenNLPTreebankParser.PARAM_CHECK_MODEL_FILE,
						"resources/models/OpenNLP.Parser.English.Check.bin.gz"),
				OpenNLPTreebankParser.PARAM_CHUNK_MODEL_FILE,
				getParameterValue(
						OpenNLPTreebankParser.PARAM_CHUNK_MODEL_FILE,
						"resources/models/OpenNLP.Chunker.English.bin.gz"),
				OpenNLPTreebankParser.PARAM_HEAD_RULES_FILE,
				getParameterValue(
						OpenNLPTreebankParser.PARAM_HEAD_RULES_FILE,
						"resources/models/OpenNLP.HeadRules.txt"));

	}
	
	public static <OUTCOME_TYPE> AnalysisEngineDescription createDataWriterAnnotator(
			Class<? extends AnnotationHandler<OUTCOME_TYPE>> annotationHandlerClass,
			Class<? extends DataWriterFactory<OUTCOME_TYPE>> dataWriterFactoryClass,
			String outputDir) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveAnalysisEngineDescription(
				DataWriterAnnotator.class, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES,
				InstanceConsumer.PARAM_ANNOTATION_HANDLER,
				annotationHandlerClass.getName(),
				DataWriterAnnotator.PARAM_DATAWRITER_FACTORY_CLASS,
				dataWriterFactoryClass.getName(),
				DataWriterAnnotator.PARAM_OUTPUT_DIRECTORY, outputDir);
	}
	
	public static <OUTCOME_TYPE> AnalysisEngineDescription createSequentialDataWriterAnnotator(
			Class<? extends SequentialAnnotationHandler<OUTCOME_TYPE>> annotationHandlerClass,
			Class<? extends SequentialDataWriterFactory<OUTCOME_TYPE>> dataWriterFactoryClass,
			String outputDir) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveAnalysisEngineDescription(
				SequentialDataWriterAnnotator.class, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES,
				SequentialInstanceConsumer.PARAM_ANNOTATION_HANDLER,
				annotationHandlerClass.getName(),
				SequentialDataWriterAnnotator.PARAM_DATAWRITER_FACTORY_CLASS,
				dataWriterFactoryClass.getName(),
				SequentialDataWriterAnnotator.PARAM_OUTPUT_DIRECTORY, outputDir);
	}
	
	public static <OUTCOME_TYPE> AnalysisEngineDescription createViterbiDataWriterAnnotator(
			Class<? extends SequentialAnnotationHandler<OUTCOME_TYPE>> annotationHandlerClass,
			Class<? extends DataWriterFactory<OUTCOME_TYPE>> delegatedDataWriterFactoryClass,
			String outputDir, Object ... configurationParameters)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveAnalysisEngineDescription(
				SequentialDataWriterAnnotator.class, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES,
				combineParams(configurationParameters,
						SequentialInstanceConsumer.PARAM_ANNOTATION_HANDLER,
						annotationHandlerClass.getName(),
						SequentialDataWriterAnnotator.PARAM_DATAWRITER_FACTORY_CLASS,
						ViterbiDataWriterFactory.class.getName(),
						SequentialDataWriterAnnotator.PARAM_OUTPUT_DIRECTORY, outputDir,
						ViterbiDataWriter.PARAM_DELEGATED_DATAWRITER_FACTORY_CLASS,
						delegatedDataWriterFactoryClass.getName(),
						ViterbiDataWriter.PARAM_OUTCOME_FEATURE_EXTRACTORS,
						new String[]{DefaultOutcomeFeatureExtractor.class.getName()}));
	}
	
	public static <OUTCOME_TYPE> AnalysisEngineDescription createClassifierAnnotator(
			Class<? extends AnnotationHandler<OUTCOME_TYPE>> annotationHandlerClass,
			String classifierJar) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveAnalysisEngineDescription(
				ClassifierAnnotator.class, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES,
				InstanceConsumer.PARAM_ANNOTATION_HANDLER,
				annotationHandlerClass.getName(),
				ClassifierAnnotator.PARAM_CLASSIFIER_JAR, classifierJar);
	}
	
	public static <OUTCOME_TYPE> AnalysisEngineDescription createSequentialClassifierAnnotator(
			Class<? extends SequentialAnnotationHandler<OUTCOME_TYPE>> annotationHandlerClass,
			String classifierJar) throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveAnalysisEngineDescription(
				SequentialClassifierAnnotator.class, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES,
				SequentialInstanceConsumer.PARAM_ANNOTATION_HANDLER,
				annotationHandlerClass.getName(),
				SequentialClassifierAnnotator.PARAM_CLASSIFIER_JAR, classifierJar);
	}
	
	public static AnalysisEngineDescription createTimeMLGoldAnnotator(boolean loadTLinks)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveAnalysisEngineDescription(
				TimeMLGoldAnnotator.class, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES,
				TimeMLGoldAnnotator.PARAM_LOAD_TLINKS, loadTLinks);
	}
	
	public static AnalysisEngineDescription createPlainTextTLINKGoldAnnotator()
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveAnalysisEngineDescription(
				PlainTextTLINKGoldAnnotator.class, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES,
				PlainTextTLINKGoldAnnotator.PARAM_TLINK_FILE_URL,
				getParameterValue(
						PlainTextTLINKGoldAnnotator.PARAM_TLINK_FILE_URL,
						"http://www.stanford.edu/~bethard/data/timebank-verb-clause.txt"));
	}
	
	public static AnalysisEngineDescription createTreebankAligningAnnotator(String treeBankDir)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveAnalysisEngineDescription(
				TreebankAligningAnnotator.class, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES,
				TreebankAligningAnnotator.PARAM_TREEBANK_DIRECTORY, treeBankDir);

	}
	
	public static AnalysisEngineDescription createTimeMLWriter(String outputDir)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveAnalysisEngineDescription(
				TimeMLWriter.class, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES,
				TimeMLWriter.PARAM_OUTPUT_DIRECTORY, outputDir);
	}
	
	public static CollectionReader createConll2005GoldReader(String conll2005DataFile)
	throws ResourceInitializationException {
		return CollectionReaderFactory.createCollectionReader(
				Conll2005GoldReader.class, TYPE_SYSTEM_DESCRIPTION,
				Conll2005GoldReader.PARAM_CONLL_2005_DATA_FILE, conll2005DataFile);
	}
	
	public static AnalysisEngineDescription createConll2005GoldAnnotator()
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveAnalysisEngineDescription(
				Conll2005GoldAnnotator.class, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES);
	}
	
	public static AnalysisEngineDescription createTreebankGoldAnnotator(boolean postTrees)
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveAnalysisEngineDescription(
				TreebankGoldAnnotator.class, TYPE_SYSTEM_DESCRIPTION, TYPE_PRIORITIES,
				TreebankGoldAnnotator.PARAM_POST_TREES, postTrees);

	}

	private static String getParameterValue(String paramName, String defaultValue) {
		String value = System.getProperty(paramName);
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}
	
	private static Object[] combineParams(Object[] oldParams, Object ... newParams) {
		Object[] combined = new Object[oldParams.length + newParams.length];
		System.arraycopy(oldParams, 0, combined, 0, oldParams.length);
		System.arraycopy(newParams, 0, combined, oldParams.length, newParams.length);
		return combined;
	}

}
