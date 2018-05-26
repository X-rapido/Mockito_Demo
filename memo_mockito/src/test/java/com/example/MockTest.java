package com.example;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

public class MockTest {

    @Mock
    List<String> mockedList;

    @Before
    public void initMocks() {

        //必须,否则注解无效
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMock() {
        mockedList.add("one");
        verify(mockedList).add("one");
    }
}
