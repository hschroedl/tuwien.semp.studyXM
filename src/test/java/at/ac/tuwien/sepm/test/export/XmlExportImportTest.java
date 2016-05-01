package at.ac.tuwien.sepm.test.export;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import at.ac.tuwien.sepm.domain.Student;
import at.ac.tuwien.sepm.export_import.XmlExportImport;
import at.ac.tuwien.sepm.helper.Constants;

/**
 * Class containing the TestCases for the XML-Importer and Exporter. Testing an
 * Importer combined with an Exporter ist rather easy: At first we generate some
 * TestData, then the TestData is exported and imported again. Finally we
 * compare the Imported data with the data we have first exported.
 * 
 * @author The SE-Team
 * @version 1.0
 */
public class XmlExportImportTest {

	/**
	 * The Spring Bean Factory, it is recreated for each new TestCase.
	 */
	private XmlBeanFactory xbf;
	
	/**
	 * Holds the absolute path of the current directory. 
	 */
	private String pathToFile;

	/**
	 * The list of Students used as testdata, also reinitialized for every new
	 * TestCase.
	 */
	private List<Student> studenten;

	/**
	 * This method is invoked before each TestCase.
	 */
	@Before
	public void setUp() throws Exception {
		ClassPathResource res = new ClassPathResource(Constants.SPRINGBEANS_TEST);
		xbf = new XmlBeanFactory(res);
		ClassPathResource currentWorkingDir = new ClassPathResource(".");
		pathToFile = currentWorkingDir.getFile().getAbsolutePath();		
		studenten = generateStudentList();
	}

	/**
	 * This method is executed after every TestCase.
	 */
	@After
	public void tearDown() throws Exception {
		studenten = null;
		xbf.destroySingletons();
	}

	/**
	 * Generate some testdata, this data is generated by the BeanFactory.
	 * 
	 * @return Testdata
	 */
	private List<Student> generateStudentList() {
		List<Student> studenten = new ArrayList<Student>();
		Student s1 = (Student) xbf.getBean("StudentAlexanderSchatten");
		s1.setId(1);
		Student s2 = (Student) xbf.getBean("StudentHubertMeixner");
		s2.setId(2);
		studenten.add(s1);
		studenten.add(s2);
		return studenten;
	}

	/**
	 * This method checks an XML-Document. It also checks for valid XML.
	 * 
	 * @param doc
	 *            The Document to check
	 */
	private void checkXml(Document doc) {
		// check root element
		Element rootEl = doc.getRootElement();
		assertThat(rootEl.getName(), is("students"));
		// check two children
		List<Student> studentenEl = (List<Student>) rootEl.elements();
		assertThat(studentenEl.size(), is(2));
		// check one Student Element
		Element sEl = (Element) studentenEl.get(0);
		assertThat(sEl.attributeValue("id"), is("1"));
		assertThat(sEl.elementText("firstname"), is("Alexander"));
		assertThat(sEl.elementText("lastname"), is("Schatten"));
		assertThat(sEl.elementText("matnr"), is("8925164"));
		assertThat(sEl.elementText("email"), is("alexander@schatten.info"));
	}

	/**
	 * TestCase for the generation of a XML file.
	 */
	@Test
	public void testGenerateXML() {
		XmlExportImport xexp = new XmlExportImport();
		xexp.generateXML(studenten);
		checkXml(xexp.getDocument());
	}

	/**
	 * TestCase for saving an XML file.
	 */
	@Test
	public void testSave() {
		final String filename = pathToFile + "/test/studenten.xml";
		XmlExportImport xexp = new XmlExportImport();
		xexp.generateXML(studenten);
		// save and re-read document in XML
		try {
			xexp.save(filename);
			xexp = null;
			xexp = new XmlExportImport();
			xexp.readXml(filename);
			checkXml(xexp.getDocument());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		// read data as List object
		List<Student> impStud = null;
		try {
			impStud = xexp.read(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//impStud.equals(studenten);
		assertThat(studenten, is(not(impStud)));
		assertThat(impStud.size(), is(studenten.size()));
		// Cycle through the imported students and compare them to the exported ones
		int i = 0;
		for (Student stud: impStud) {
			assertThat(stud.getId(), is(studenten.get(i).getId()));
			assertThat(stud.getMatnr(), is(studenten.get(i).getMatnr()));
			assertThat(stud.getFirstname(), is(studenten.get(i).getFirstname()));
			assertThat(stud.getLastname(), is(studenten.get(i).getLastname()));
			assertThat(stud.getEmail(), is(studenten.get(i).getEmail()));
			i++;
		}
	}

}