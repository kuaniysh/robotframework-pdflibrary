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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

@RobotKeywords
public class PDFKeywords {

	public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";

	private PdfReader reader;
	private Map<Integer, String> pdfData;

	@RobotKeyword
	public void parsePdf(String filename) throws IOException {
		reader = new PdfReader(filename);
		System.out.println("Reading file "+filename);

		pdfData = new HashMap<Integer, String>();
		int numberOfPages = reader.getNumberOfPages();
		for (int page = 1; page <= numberOfPages; page++) {
			System.out.println("Reading page "+page);
			String textFromPage = PdfTextExtractor
					.getTextFromPage(reader, page);
			pdfData.put(page, textFromPage);

		}
	}

	@RobotKeyword
	public void pdfShouldContain(String expectedValue) {
		Collection<String> values = pdfData.values();
		collectionShouldContain(expectedValue, values);
	}

	@RobotKeyword
	public void pdfShouldContain(String expectedValue, String ignoreLinebreaks) {
		if (StringUtils.isEmpty(ignoreLinebreaks)) pdfShouldContain(expectedValue);
		Collection<String> values = pdfData.values();
		values = removeLinebreaks(values);
		collectionShouldContain(expectedValue, values);
	}

	@RobotKeyword
	public void pdfShouldContain(String expectedValue, String ignoreLinebreaks, String ignoreCase) {
		if (StringUtils.isEmpty(ignoreCase)) pdfShouldContain(expectedValue, ignoreLinebreaks);
		Collection<String> values = pdfData.values();
		values = removeLinebreaks(values);
		collectionShouldContain(expectedValue, values, StringUtils.isNotEmpty(ignoreCase));
	}
	
	private void collectionShouldContain(String expectedValue, Collection<String> values) {
		collectionShouldContain(expectedValue, values, false);
	}
	
	private void collectionShouldContain(String expectedValue, Collection<String> values, boolean ignoreCase) {
		for (String content : values) {
			if (ignoreCase?StringUtils.containsIgnoreCase(content, expectedValue):StringUtils.contains(content, expectedValue)) {
				return;
			}
		}
		throw new RuntimeException("could not find " + expectedValue + " in "
				+ pdfData);
	}

	protected Collection<String> removeLinebreaks(Collection<String> values) {
		List<String> contentWithoutLinebreacks = new ArrayList<String>();
		for (String string : values) {
			String reformattedString = string.replaceAll("\\n", " ");
			while (reformattedString.contains("  ")) {
				reformattedString = reformattedString.replaceAll("  ", " ");
			}
			contentWithoutLinebreacks.add(reformattedString);
		}
		values = contentWithoutLinebreacks;
		return values;
	}

}
