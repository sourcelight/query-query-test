package org.query.calc;

import static org.query.calc.ResultComparator.assertFilesEqual;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.TemporaryFolder;
import org.query.repository.T1Repository;
import org.query.repository.T2Repository;
import org.query.repository.T3Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class ,
		classes = {QueryCalcImpl.class} )		
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "org.query.repository")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@EntityScan(basePackages = "org.query.model.entity")
@PropertySource("classpath:application.properties")
public class QueryCalcTest {
	
	@Autowired
	@Qualifier("t1Repository")
	private T1Repository t1Repository;
	
	@Autowired
	@Qualifier("t2Repository")
	private T2Repository t2Repository;
	
	@Autowired
	@Qualifier("t3Repository")
	private T3Repository t3Repository;
		
	
	
	@Autowired	
	private QueryCalc queryCalc;

    public void doTest(String testName, QueryCalc queryCalc, String caseName) throws IOException, URISyntaxException {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        ClassLoader classLoader = getClass().getClassLoader();

        temporaryFolder.create();
        File actualResultFile = temporaryFolder.newFile("actual-result");
        Path actualResult = actualResultFile.toPath();
        try {
            Path t1 = Path.of(classLoader.getResource(caseName + "/t1").toURI());
            Path t2 = Path.of(classLoader.getResource(caseName + "/t2").toURI());
            Path t3 = Path.of(classLoader.getResource(caseName + "/t3").toURI());
            Path expectedResult = Path.of(classLoader.getResource(caseName + "/expected-result").toURI());                       

            queryCalc.select(t1, t2, t3, actualResult);

            assertFilesEqual(() -> testName, expectedResult, actualResult);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            actualResultFile.delete();
            temporaryFolder.delete();
        }
    }
    
    
    @BeforeEach
    public void executedBeforeEach() {       
        t1Repository.deleteAll();
        t2Repository.deleteAll();
        t3Repository.deleteAll();
    }

    @Test
    public void testCase0() throws IOException, URISyntaxException {
        doTest("case-0", queryCalc, "case-0");
    }

    @Test
    public void testCase1() throws IOException, URISyntaxException {
        doTest("case-1", queryCalc, "case-1");
    }

    @Test
    public void testCase2() throws IOException, URISyntaxException {
        doTest("case-2", queryCalc, "case-2");
    }

    @Test
    public void testCase3() throws IOException, URISyntaxException {
        doTest("case-3", queryCalc, "case-3");
    }
}
