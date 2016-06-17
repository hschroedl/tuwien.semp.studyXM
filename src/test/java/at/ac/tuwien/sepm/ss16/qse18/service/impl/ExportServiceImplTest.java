package at.ac.tuwien.sepm.ss16.qse18.service.impl;

import at.ac.tuwien.sepm.ss16.qse18.dao.DaoException;
import at.ac.tuwien.sepm.ss16.qse18.domain.*;
import at.ac.tuwien.sepm.ss16.qse18.service.QuestionService;
import at.ac.tuwien.sepm.ss16.qse18.service.ResourceQuestionService;
import at.ac.tuwien.sepm.ss16.qse18.service.ServiceException;
import at.ac.tuwien.sepm.ss16.qse18.service.TopicService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FileOutputStream.class, File.class, ExportServiceImpl.class, ZipEntry.class,
    ZipOutputStream.class, ObjectOutputStream.class}) @PowerMockIgnore("javax.management.*")
public class ExportServiceImplTest extends ImportExportBaseTest {
    private ExportServiceImpl exportService;
    @Mock private ZipOutputStream mockZipOutputStream;
    @Mock private FileOutputStream mockFileOutputStream;
    @Mock private ZipEntry mockZipEntry;
    @Mock private ObjectOutputStream mockObjectOutputStream;
    @Mock private FileInputStream mockFileInputStream;
    @Mock private TopicService mockTopicService;
    @Mock private QuestionService mockQuestionService;
    @Mock private ResourceQuestionService mockResourceQuestionService;
    @Mock private File mockFile;

    @Before public void setUp() throws Exception {
        exportService = new ExportServiceImpl();

        createDummySubjectWithDummyRelations();

        exportService.setSubject(testSubject);
        exportService.setTopicService(mockTopicService);
        exportService.setResourceQuestionService(mockResourceQuestionService);
        exportService.setQuestionService(mockQuestionService);

        prepareDaoAndServiceMocks();
    }

    private void prepareDaoAndServiceMocks() throws ServiceException, DaoException {
        when(mockTopicService.getTopicsFromSubject(any(Subject.class))).thenReturn(testTopics);
        when(mockResourceQuestionService.getResourceFromQuestion(any(Question.class)))
            .thenReturn(testResource);
        when(mockQuestionService.getQuestionsFromTopic(any(Topic.class))).thenReturn(testQuestions1)
            .thenReturn(testQuestions2);
        when(mockQuestionService.getCorrespondingAnswers(any(Question.class)))
            .thenReturn(testAnswers1).thenReturn(testAnswers2);
    }

    @Test(expected = ServiceException.class) public void test_export_withInvalidPath_Fail()
        throws Exception {
        whenNew(FileOutputStream.class).withArguments(anyString())
            .thenThrow(FileNotFoundException.class);
        exportService.export("invalid path");
    }

    @Test(expected = ServiceException.class) public void test_export_withNoSubjectSelected_Fail()
        throws Exception {
        exportService.setSubject(null);
        exportService.export("valid path");
    }

    @Test(expected = ServiceException.class)
    public void test_export_errorWhenTryingToCreateDirectoryInZip_Fail() throws Exception {
        whenNew(ZipEntry.class).withArguments(anyString()).thenReturn(mockZipEntry);
        whenNew(ZipOutputStream.class).withArguments(any(ZipEntry.class))
            .thenReturn(mockZipOutputStream);
        doThrow(IOException.class).when(mockZipOutputStream).putNextEntry(any(ZipEntry.class));
        whenNew(FileOutputStream.class).withAnyArguments().thenReturn(mockFileOutputStream);

        exportService.export("valid path");
    }

    @Test(expected = ServiceException.class)
    public void test_export_errorWhileTryingToWriteSubjectFile_Fail() throws Exception {
        setMocksUp();
        doThrow(IOException.class).when(mockObjectOutputStream).writeObject(any());

        exportService.export("valid path");
    }

    @Test(expected = ServiceException.class)
    public void test_export_errorWhileTryingToWriteToZipFile_Fail() throws Exception {
        setMocksUp();
        whenNew(FileInputStream.class).withParameterTypes(File.class).withArguments(any(File.class))
            .thenReturn(mockFileInputStream);
        when(mockFileInputStream.read(any())).thenThrow(IOException.class);

        exportService.export("valid path");
    }

    @Test public void test_export_withValidInputs() throws Exception {
        setMocksUp();
        whenNew(FileInputStream.class).withParameterTypes(File.class).withArguments(any(File.class))
            .thenReturn(mockFileInputStream);
        when(mockFileInputStream.read(any())).thenReturn(0).thenReturn(-1);
        whenNew(File.class).withArguments(anyString()).thenReturn(mockFile);
        when(mockFile.exists()).thenReturn(true);

        exportService.export("valid path");
    }

    @Test(expected = ServiceException.class)
    public void test_export_errorWhileClosingZipOutputStream_Fail() throws Exception {
        setMocksUp();
        whenNew(FileInputStream.class).withParameterTypes(File.class).withArguments(any(File.class))
            .thenReturn(mockFileInputStream);
        when(mockFileInputStream.read(any())).thenReturn(0).thenReturn(-1);
        whenNew(File.class).withArguments(anyString()).thenReturn(mockFile);
        when(mockFile.exists()).thenReturn(true);
        doThrow(IOException.class).when(mockZipOutputStream).close();

        exportService.export("valid path");
    }

    private void setMocksUp() throws Exception {
        whenNew(ZipEntry.class).withArguments(anyString()).thenReturn(mockZipEntry);
        whenNew(ZipOutputStream.class).withArguments(any(ZipEntry.class))
            .thenReturn(mockZipOutputStream);
        whenNew(FileOutputStream.class).withArguments(anyString()).thenReturn(mockFileOutputStream);
        whenNew(ObjectOutputStream.class).withAnyArguments().thenReturn(mockObjectOutputStream);
    }

    @After public void tearDown() throws Exception {
        // nothing to tear down
    }

}
