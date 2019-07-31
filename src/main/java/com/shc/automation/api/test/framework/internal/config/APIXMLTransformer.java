/**
 * 
 */
package com.shc.automation.api.test.framework.internal.config;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * @author spoojar
 *
 */
public enum APIXMLTransformer {
	INSTANCE;
	private final Logger log = Logger.getLogger(this.getClass());

	@SuppressWarnings("unchecked")
	public <T> T transform(String xmlFile, Class<T> type) {
		if (StringUtils.isBlank(xmlFile)) {
			log.error("Invalid/Blank file name for XML Parsing");
			return null;
		}
		InputStream xmlStream = this.getClass().getClassLoader().getResourceAsStream(xmlFile);
		if (xmlStream == null) {
			log.warn("Not able to load File! File not available :" + xmlFile);
			return null;
		}
		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(type);

			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setFeature("http://apache.org/xml/features/validation/schema", false);
			spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			XMLReader xmlReader = spf.newSAXParser().getXMLReader();

			InputSource inputSource = new InputSource(this.getClass().getClassLoader().getResourceAsStream(xmlFile));
			SAXSource source = new SAXSource(xmlReader, inputSource);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			return (T) jaxbUnmarshaller.unmarshal(source);
		} catch (JAXBException je) {
			log.error("Error in parsing XML File :" + xmlFile, je);

		} catch (SAXNotRecognizedException e) {
			log.error("Error in parsing XML File :" + xmlFile, e);
		} catch (SAXNotSupportedException e) {
			log.error("Error in parsing XML File :" + xmlFile, e);
		} catch (ParserConfigurationException e) {
			log.error("Error in parsing XML File :" + xmlFile, e);
		} catch (SAXException e) {
			log.error("Error in parsing XML File :" + xmlFile, e);
		} finally {
			if (xmlStream != null) {
				try {
					xmlStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
