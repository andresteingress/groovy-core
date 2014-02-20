/*
 * Copyright 2003-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.test.GroovyAssert;
import junit.framework.TestCase;

import java.util.logging.Logger;

/**
 * A default JUnit 3 TestCase in Groovy.
 *
 * In case JUnit 4 is used, there is a {@code GroovyTestCase} equivalent, see {@link groovy.test.GroovyAssert}.
 *
 * @see groovy.test.GroovyAssert
 *
 * @author <a href="mailto:bob@werken.com">bob mcwhirter</a>
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @author Dierk Koenig (the notYetImplemented feature, changes to shouldFail)
 * @author Andre Steingress
 */
public class GroovyTestCase extends TestCase {

    protected static Logger log = Logger.getLogger(GroovyTestCase.class.getName());

    private static int counter;

    public static final String TEST_SCRIPT_NAME_PREFIX = "TestScript";

    private boolean useAgileDoxNaming = false;

    /**
     * Overload the getName() method to make the test cases look more like AgileDox
     * (thanks to Joe Walnes for this tip!)
     */
    public String getName() {
        if (useAgileDoxNaming) {
            return super.getName().substring(4).replaceAll("([A-Z])", " $1").toLowerCase();
        } else {
            return super.getName();
        }
    }

    public String getMethodName() {
        return super.getName();
    }

    /**
     * see {@link groovy.test.GroovyAssert#assertArrayEquals(Object[], Object[])}
     */
    protected void assertArrayEquals(Object[] expected, Object[] value) {
        GroovyAssert.assertArrayEquals(expected, value);
    }

    /**
     * see {@link groovy.test.GroovyAssert#assertLength(int, char[])}
     */
    protected void assertLength(int length, char[] array) {
        GroovyAssert.assertLength(length, array);
    }

    /**
     * see {@link groovy.test.GroovyAssert#assertLength(int, int[])}
     */
    protected void assertLength(int length, int[] array) {
        GroovyAssert.assertLength(length, array);
    }

    /**
     * see {@link groovy.test.GroovyAssert#assertLength(int, Object[])}
     */
    protected void assertLength(int length, Object[] array) {
        GroovyAssert.assertLength(length, array);
    }

    /**
     * see {@link groovy.test.GroovyAssert#assertContains(char, char[])}
     */
    protected void assertContains(char expected, char[] array) {
        GroovyAssert.assertContains(expected, array);
    }

    /**
     * see {@link groovy.test.GroovyAssert#assertContains(int, int[])}
     */
    protected void assertContains(int expected, int[] array) {
        GroovyAssert.assertContains(expected, array);
    }

    /**
     * @see {@link groovy.test.GroovyAssert#assertToString(Object, String)}
     */
    protected void assertToString(Object value, String expected) {
        GroovyAssert.assertToString(value, expected);
    }

    /**
     * @see {@link groovy.test.GroovyAssert#assertInspect(Object, String)}
     */
    protected void assertInspect(Object value, String expected) {
        GroovyAssert.assertInspect(value, expected);
    }

    /**
     * see {@link groovy.test.GroovyAssert#assertScript(String)}
     */
    protected void assertScript(final String script) throws Exception {
        GroovyAssert.assertScript(script);
    }

    // TODO should this be synchronised?
    protected String getTestClassName() {
        return TEST_SCRIPT_NAME_PREFIX + getMethodName() + (counter++) + ".groovy";
    }

    /**
     * see {@link groovy.test.GroovyAssert#shouldFail(groovy.lang.Closure)}
     */
    protected String shouldFail(Closure code) {
        return GroovyAssert.shouldFail(code).getMessage();
    }

    /**
     * see {@link groovy.test.GroovyAssert#shouldFail(Class, groovy.lang.Closure)}
     */
    protected String shouldFail(Class clazz, Closure code) {
        return GroovyAssert.shouldFail(clazz, code).getMessage();
    }

    /**
     * see {@link groovy.test.GroovyAssert#shouldFailWithCause(Class, groovy.lang.Closure)}
     */
    protected String shouldFailWithCause(Class clazz, Closure code) {
        return GroovyAssert.shouldFailWithCause(clazz, code).getMessage();
    }

    /**
     * see {@link groovy.test.GroovyAssert#shouldFail(Class, String)}
     */
    protected String shouldFail(Class clazz, String script) {
        return GroovyAssert.shouldFail(clazz, script).getMessage();
    }

    /**
     * see {@link groovy.test.GroovyAssert#shouldFail(String)}
     */
    protected String shouldFail(String script) {
        return GroovyAssert.shouldFail(script).getMessage();
    }

    /**
     * Returns a copy of a string in which all EOLs are \n.
     */
    protected String fixEOLs(String value) {
        return value.replaceAll("(\\r\\n?)|\n", "\n");
    }

    /**
     * see {@link groovy.test.GroovyAssert#notYetImplemented(java.lang.Object)}
     */
    public static boolean notYetImplemented(Object caller) {
        return GroovyAssert.notYetImplemented(caller);
    }

    /**
     * Convenience method for subclasses of GroovyTestCase, identical to
     * <pre> GroovyTestCase.notYetImplemented(this); </pre>.
     *
     * @return <false> when not itself already in the call stack
     * @see #notYetImplemented(java.lang.Object)
     */
    public boolean notYetImplemented() {
        return notYetImplemented(this);
    }

    public static void assertEquals(String message, Object expected, Object actual) {
        GroovyAssert.assertEquals(message, expected, actual);
    }

    public static void assertEquals(Object expected, Object actual) {
        GroovyAssert.assertEquals(expected, actual);
    }

    public static void assertEquals(String expected, String actual) {
        GroovyAssert.assertEquals(expected, actual);
    }
}
