package com.example;

import com.example.bean.Person;
import com.example.dao.PersonDao;
import com.example.service.PersonService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PersonServiceTest {

    private PersonDao mockDao;
    private PersonService personService;

    @Before
    public void setUp() throws Exception {

        // 模拟PersonDao对象
        mockDao = mock(PersonDao.class);
        when(mockDao.getPerson(1)).thenReturn(new Person(1, "Person1"));
        when(mockDao.update(isA(Person.class))).thenReturn(true);

        personService = new PersonService(mockDao);
    }

    @Test
    public void testUpdate() {
        boolean result = personService.update(1, "new name");
        Assert.assertTrue("must true", result);

        //验证是否执行过一次getPerson(1)
        verify(mockDao, times(1)).getPerson(eq(1));

        // 验证是否执行过一次update
        verify(mockDao, times(1)).update(isA(Person.class));
    }

    @Test
    public void testUpdateNotFind(){
        boolean result = personService.update(2, "new name");
        Assert.assertFalse("must true", result);

        // 验证是否执行过一次getPerson(1)
        verify(mockDao, times(1)).getPerson(eq(1));

        //验证是否执行过一次update
        verify(mockDao, never()).update(isA(Person.class));
    }
}
