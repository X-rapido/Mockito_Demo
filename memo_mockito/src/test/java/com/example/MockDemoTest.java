package com.example;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.verification.Timeout;
import org.mockito.verification.VerificationMode;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

public class MockDemoTest {

    /**
     * 验证行为
     */
    @Test
    public void testVerify(){
        // mock creation
        List mockedList = mock(List.class);

        // using mock object
        mockedList.add("one");
        mockedList.add("two");
        mockedList.add("two");
        mockedList.clear();

        // verification
        //验证是否调用过一次 mockedList.add("one")方法，若不是（0次或者大于一次），测试将不通过
        verify(mockedList).add("one");

        // 验证调用过2次 mockedList.add("two")方法，若不是，测试将不通过
        verify(mockedList, times(2)).add("two");

        // 验证是否调用过一次 mockedList.clear()方法，若没有（0次或者大于一次），测试将不通过
        verify(mockedList).clear();
    }

    /**
     * Stubbing
     */
    @Test
    public void testStubbing(){
        // 你可以mock具体的类，而不仅仅是接口
        LinkedList mockedList = mock(LinkedList.class);

        // 设置桩
        when(mockedList.get(0)).thenReturn("first");
        when(mockedList.get(1)).thenThrow(new RuntimeException());

        // 打印 "first"
        System.out.println(mockedList.get(0));

        // 这里会抛runtime exception
    //        System.out.println(mockedList.get(1));

        // 这里会打印 "null" 因为 get(999) 没有设置
        System.out.println(mockedList.get(999));

        // Although it is possible to verify a stubbed invocation, usually it's just redundant
        // If your code cares what get(0) returns, then something else breaks (often even before verify() gets executed).
        // If your code doesn't care what get(0) returns, then it should not be stubbed. Not convinced? See here.
        verify(mockedList).get(0);
    }

    /**
     * 参数匹配
     */
    @Test
    public void testArgumentMatcher() {
        LinkedList mockedList = mock(LinkedList.class);

        // 用内置的参数匹配器来stub
        when(mockedList.get(anyInt())).thenReturn("element");

        // 打印 "element"
        System.out.println(mockedList.get(999));

        // 你也可以用参数匹配器来验证，此处测试通过
        verify(mockedList).get(anyInt());

        // 此处测试将不通过，因为没调用get(33)
        verify(mockedList).get(eq(33));
    }

    /**
     * 验证准确的调用次数，最多、最少、从未等
     */
    @Test
    public void testInvocationTimes() {
        LinkedList mockedList = mock(LinkedList.class);

        // using mock
        mockedList.add("once");

        mockedList.add("twice");
        mockedList.add("twice");

        mockedList.add("three times");
        mockedList.add("three times");
        mockedList.add("three times");

        // 下面两个是等价的， 默认使用times(1)
        verify(mockedList).add("once");
        verify(mockedList, times(1)).add("once");

        // 验证准确的调用次数
        verify(mockedList, times(2)).add("twice");
        verify(mockedList, times(3)).add("three times");

        // 从未调用过. never()是times(0)的别名
        verify(mockedList, never()).add("never happened");

        // 用atLeast()/atMost()验证
        verify(mockedList, atLeastOnce()).add("three times");

        // 下面这句将不能通过测试
        verify(mockedList, atLeast(2)).add("five times");
        verify(mockedList, atMost(5)).add("three times");
    }

    /**
     * 为void方法抛异常
     */
    @Test
    public void testVoidMethodsWithExceptions() {
        LinkedList mockedList = mock(LinkedList.class);
        doThrow(new RuntimeException()).when(mockedList).clear();

        // 当调用clear方法时，抛RuntimeException异常
        mockedList.clear();
    }

    /**
     * 验证调用顺序
     */
    @Test
    public void testVerificationInOrder() {
        // A. 单一模拟的方法必须以特定顺序调用
        List singleMock = mock(List.class);

        // 使用单个mock对象
        singleMock.add("was added first");
        singleMock.add("was added second");

        // 创建inOrder
        InOrder inOrder = inOrder(singleMock);

        // 验证调用次数，若是调换两句，将会出错，因为singleMock.add("was added first")是先调用的
        inOrder.verify(singleMock).add("was added first");
        inOrder.verify(singleMock).add("was added second");

        // 多个mock对象
        List firstMock = mock(List.class);
        List secondMock = mock(List.class);

        // using mocks
        firstMock.add("was called first");
        secondMock.add("was called second");

        // 创建多个mock对象的inOrder
        inOrder = inOrder(firstMock, secondMock);

        // 验证firstMock先于secondMock调用
        inOrder.verify(firstMock).add("was called first");
        inOrder.verify(secondMock).add("was called second");
    }

    /**
     * 验证mock对象没有产生过交互
     */
    @Test
    public void testInteractionNeverHappened() {
        List mockOne = mock(List.class);
        List mockTwo = mock(List.class);

        // 测试通过
        verifyZeroInteractions(mockOne, mockTwo);

        mockOne.add("");

        // 测试不通过，因为mockTwo已经发生过交互了
        verifyZeroInteractions(mockOne, mockTwo);
    }

    /**
     * 查找是否有未验证的交互
     */
    @Test
    public void testFindingRedundantInvocations() throws Exception {
        List mockedList = mock(List.class);

        //using mocks
        mockedList.add("one");
        mockedList.add("two");

        verify(mockedList).add("one");

        //验证失败，因为mockedList.add("two")尚未验证
        verifyNoMoreInteractions(mockedList);
    }

    /**
     * 根据调用顺序设置不同的stubbing
     */
    @Test
    public void testStubbingConsecutiveCalls() {

        MockInterfaceTest mock = mock(MockInterfaceTest.class);
        when(mock.someMethod("some arg")).thenThrow(new RuntimeException("")).thenReturn("foo");
    //        when(mock.someMethod("some arg")).thenReturn("aaa").thenReturn("foo");

        // 第一次调用，抛RuntimeException
        mock.someMethod("some arg");

        // 第二次调用返回foo
        System.out.println(mock.someMethod("some arg"));

        // 后续继续调用，返回“foo”，以最后一个stub为准
        System.out.println(mock.someMethod("some arg"));

        // 下面是一个更简洁的写法
        when(mock.someMethod("some arg")).thenReturn("one", "two", "three");
        System.out.println(mock.someMethod("some arg"));    // one
        System.out.println(mock.someMethod("some arg"));    // two
        System.out.println(mock.someMethod("some arg"));    // three
    }


    /**
     * spy监视真正的对象
     */
    @Test
    public void testSpy() {
        List list = new LinkedList();
        List spy = spy(list);

        // 可选的，你可以stub某些方法
        when(spy.size()).thenReturn(100);

        // 调用"真正"的方法
        spy.add("one");
        spy.add("two");

        // 打印one
        System.out.println(spy.get(0));

        // size()方法被stub了，打印100
        System.out.println(spy.size());

        // 可选，验证spy对象的行为
        verify(spy).add("one");
        verify(spy).add("two");

        //下面写法有问题，spy.get(10)会抛IndexOutOfBoundsException异常
        when(spy.get(10)).thenReturn("foo");

        // 可用以下方式
        doReturn("foo").when(spy).get(10);
    }

    /**
     * 为未stub的方法设置默认返回值
     */
    @Test
    public void testDefaultValue() {

        List listOne = mock(List.class, Mockito.RETURNS_SMART_NULLS);

        List listTwo = mock(List.class, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                // TODO: return default value here
                return null;
            }
        });

        // lambda表达式写法
        List listThree = mock(List.class, (invocation)-> "听风");

        System.out.println(listOne.get(10));
        System.out.println(listTwo.get(10));
        System.out.println(listThree.get(10));
    }

    /**
     * 参数捕捉
     */
    @Test
    public void testCapturingArguments() {
        List mockedList = mock(List.class);

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        mockedList.add("John");

        // 验证后再捕捉参数
        verify(mockedList).add(argument.capture());

        // 验证参数
        Assert.assertEquals("John", argument.getValue());
    }

    /**
     * 重置mocks
     */
    @Test
    public void testReset() {
        List mock = mock(List.class);
        when(mock.size()).thenReturn(10);
        mock.add(1);
        reset(mock);
        //从这开始，之前的交互和stub将全部失效
    }

    /**
     * 序列化mock。很少用于单元测试。
     */
    @Test
    public void testSerializableMocks() {
        List serializableMock = mock(List.class, withSettings().serializable());
    }

    /**
     * 超时验证
     */
    @Test
    public void testTimeout(){
        TimeMockTest mock = mock(TimeMockTest.class);

        // 测试程序将会在下面这句阻塞100毫秒，timeout的时候再进行验证是否执行过someMethod()
        verify(mock, timeout(100)).someMethod();

        // 和上面代码等价
        verify(mock, timeout(100).times(1)).someMethod();

        // 阻塞100ms，timeout的时候再验证是否刚好执行了2次
        verify(mock, timeout(100).times(2)).someMethod();

        // timeout的时候，验证至少执行了2次
        verify(mock, timeout(100).atLeast(2)).someMethod();

        // timeout时间后，用自定义的检验模式验证someMethod()
        VerificationMode yourOwnVerificationMode = new VerificationMode() {

            @Override
            public void verify(VerificationData data) {
                System.out.println(data);
            }

            @Override
            public VerificationMode description(String description) {
                System.out.println(description);
                return null;
            }
        };

        verify(mock, new Timeout(100, yourOwnVerificationMode)).someMethod();
    }

    /**
     * 查看是否mock或者spy
     */
    @Test
    public void testMockAndSpy(){
        TimeMockTest mock = mock(TimeMockTest.class);

        System.out.println(Mockito.mockingDetails(mock).isMock());  // true
        System.out.println(Mockito.mockingDetails(mock).isSpy());   // false
    }
}
