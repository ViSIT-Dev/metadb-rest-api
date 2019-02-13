package rest.persistence.repository;

import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.springframework.beans.factory.annotation.Autowired;
import rest.BaseWebTest;

import static org.junit.Assert.*;

public class TemplateRepositoryTest extends BaseWebTest {

    @Test
    @Ignore
    public void testTriggerTemplates() {
        this.templateRepository.triggerTemplateGeneration();
    }

    @Test
    @Ignore
    public void testTriggerTemplates2() {
        this.templateRepository.triggerTemplateGeneration2();
    }

    @Override
    public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException, RepositoryConfigException {

    }
}