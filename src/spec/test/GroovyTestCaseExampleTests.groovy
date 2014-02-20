import groovy.transform.NotYetImplemented

class GroovyTestCaseExampleTests extends GroovyTestCase {

    // tag::assertions[]
    void testAssertions() {
        assertTrue(1 == 1)
        assertEquals("test", "test")

        def x = "42"
        assertNotNull "x must not be null", x
        assertNull null

        assertSame x, x
    }
    // end::assertions[]

    // tag::assertScript[]
    void testScriptAssertions() {
        assertScript '''
            def x = 1
            def y = 2

            assert x + y == 3
        '''
    }
    // end::assertScript[]

    // tag::should_fail_without_class[]
    void testInvalidIndexAccess1() {
        def numbers = [1,2,3,4]
        shouldFail {
            numbers.get(4)
        }
    }
    // end::should_fail_without_class[]

    // tag::should_fail_with_class[]
    void testInvalidIndexAccess2() {
        def numbers = [1,2,3,4]
        shouldFail IndexOutOfBoundsException, {
            numbers.get(4)
        }
    }
    // end::should_fail_with_class[]

    // tag::should_fail_with_msg[]
    void testInvalidIndexAccess3() {
        def numbers = [1,2,3,4]
        def msg = shouldFail IndexOutOfBoundsException, {
            numbers.get(4)
        }
        assert msg.contains('Index: 4, Size: 4')
    }
    // end::should_fail_with_msg[]

    // tag::not_yet_implemented[]
    void testNotYetImplemented1() {
        if (notYetImplemented()) return   // <1>

        assert 1 == 2                     // <2>
    }
    // end::not_yet_implemented[]

    // tag::not_yet_implemented_ast[]
    @NotYetImplemented
    void testNotYetImplemented2() {
        assert 1 == 2
    }
    // end::not_yet_implemented_ast[]

}
