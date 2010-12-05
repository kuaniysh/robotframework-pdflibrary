/*
 * Copyright 2010 codecentric AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.codecentric.robot.pdf;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;


public class PDFKeywordsTest {

	private PDFKeywords pdfLibrary;

	final static String SUCCESS_STORY_PDF = "effektive-neukundengewinnung.pdf";
	final static String ARTICLE_PDF = "business-case-fuer-agilitaet.pdf";
	final static String FIELD_GEBURTSNAME = "profil_geburtsdatum";
	final static String TITLE = "SUCCESS STORY";

	@Before
	public void setup() {
		pdfLibrary = new PDFKeywords();
	}
	
	@Test
	public void shouldParseField() throws IOException {
		pdfLibrary.parsePdf(SUCCESS_STORY_PDF);
		pdfLibrary.pdfShouldContain(TITLE);
	}
	
	@Test(expected=IOException.class)
	public void shouldFailOnNoFile() throws IOException {
		pdfLibrary.parsePdf(SUCCESS_STORY_PDF+"XXX");
	}

	@Test(expected=Throwable.class)
	public void shouldFailOnUnknownText() throws IOException {
		pdfLibrary.parsePdf(SUCCESS_STORY_PDF);
		pdfLibrary.pdfShouldContain(TITLE+"XXX");
	}
	
	@Test
	public void shouldFindWrappedLines() throws IOException {
		pdfLibrary.parsePdf(SUCCESS_STORY_PDF);
		pdfLibrary.pdfShouldContain("Ziel ist, innerhalb von 3 Jahren jeden Monat mehr als 100.000 "+
									"neue Adressen und über 10.000 neue Kunden zu gewinnen.", "ignore linebreaks");
	}

	@Test
	public void shouldFindWrappedHeaders() throws IOException {
		pdfLibrary.parsePdf(ARTICLE_PDF);
		pdfLibrary.pdfShouldContain("Business Case für Agilität", "ignore linebreaks");
	}

	@Test
	public void shouldIgnoreCase() throws IOException {
		pdfLibrary.parsePdf(ARTICLE_PDF);
		pdfLibrary.pdfShouldContain("STUDIEN gehen DAVON aus, DASS fast ZWEI Drittel DER Funk- "+
				"tionen in einer Anwendung selten oder überhaupt nicht "+
				"GENUTZT WERDEN", "ignore linebreaks", "ignore case");
	}
	
	
	@Test
	public void shouldNotTouchWhenNoLinebreaks() {
		Collection<String> values = Arrays.asList(new String[] {"a", "b", "c"}); 
		values = pdfLibrary.removeLinebreaks(values);
		assertTrue(values.contains("a"));
		assertTrue(values.contains("b"));
		assertTrue(values.contains("c"));
	}

	@Test
	public void shouldRemoveLinebreaks() {
		Collection<String> values = Arrays.asList(new String[] {"a\nb", "c\nd"}); 
		values = pdfLibrary.removeLinebreaks(values);
		assertTrue(values.contains("a b"));
		assertTrue(values.contains("c d"));
	}

	@Test
	public void shouldRemoveDoubleSpaces() {
		Collection<String> values = Arrays.asList(new String[] {"a  b", "c   d", "e\n f", "g \nh"}); 
		values = pdfLibrary.removeLinebreaks(values);
		assertTrue(values.contains("a b"));
		assertTrue(values.contains("c d"));
		assertTrue(values.contains("e f"));
		assertTrue(values.contains("g h"));
	}
}

