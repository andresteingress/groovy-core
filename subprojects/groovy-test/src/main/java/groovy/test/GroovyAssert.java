/*
 * Copyright 2003-2013 the original author or authors.
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
package groovy.test;

import groovy.lang.Closure;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

/**
 * <p>{@code GroovyAssert} is supposed to be used in JUnit 4 tests. By inheriting from JUnit's {@link org.junit.Assert}
 * class this class extends the set of static JUnit assertion methods with custom Groovy assertion and helper methods and
 * acts as central host for them.
 * </p>
 *
 * <p>
 * {@code GroovyAssert} methods can either be used by fully qualifying the static method like
 *
 * <pre>
 *     groovy.test.GroovyAssert.shouldFail { ... }
 * </pre>
 *
 * or by importing the static methods with one ore more static imports
 *
 * <pre>
 *     import static groovy.test.GroovyAssert.shouldFail
 *     import static groovy.test.GroovyAssert.assertNotNull
 * </pre>
 *
 * </p>
 *
 * @author Paul King
 * @author Andre Steingress
 *
 * @since 2.3
 */
public class GroovyAssert extends org.junit.Assert {

    private static final Logger log = Logger.getLogger(GroovyAssert.class.getName());
    private static final int MAX_NESTED_EXCEPTIONS = 10;

    private static int counter;
    public static final String TEST_SCRIPT_NAME_PREFIX = "TestScript";

    /**
     * @return a generic script name to be used by {@code GroovyShell#evaluate} calls.
     */
    protected static String genericScriptName() {
        return TEST_SCRIPT_NAME_PREFIX + (counter++) + ".groovy";
    }

    /**
     * Asserts that the arrays are equivalent and contain the same values
     *
     * @param value the array value
     * @param expected the expected array
     */
    public static void assertArrayEquals(Object[] expected, Object[] value) {
        String message = "expected array: " + InvokerHelper.toString(expected) + " value array: " + InvokerHelper.toString(value);
        assertNotNull(message + ": expected should not be null", expected);
        assertNotNull(message + ": value should not be null", value);
        assertEquals(message, expected.length, value.length);
        for (int i = 0, size = expected.length; i < size; i++) {
            assertEquals("value[" + i + "] when " + message, expected[i], value[i]);
        }
    }

    /**
     * Asserts that the array of characters has a given length
     *
     * @param length expected length
     * @param array  the array
     */
    public static void assertLength(int length, char[] array) {
        assertEquals(length, array.length);
    }

    /**
     * Asserts that the array of ints has a given length
     *
     * @param length expected length
     * @param array  the array
     */
    public static void assertLength(int length, int[] array) {
        assertEquals(length, array.length);
    }

    /**
     * Asserts that the array of objects has a given length
     *
     * @param length expected length
     * @param array  the array
     */
    public static void assertLength(int length, Object[] array) {
        assertEquals(length, array.length);
    }

    /**
     * Asserts that the array of characters contains a given char
     *
     * @param expected expected character to be found
     * @param array    the array
     */
    public static void assertContains(char expected, char[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == expected) {
                return;
            }
        }

        StringBuilder message = new StringBuilder();

        message.append(expected).append(" not in {");

        for (int i = 0; i < array.length; ++i) {
            message.append("'").append(array[i]).append("'");

            if (i < (array.length - 1)) {
                message.append(", ");
            }
        }

        message.append(" }");

        fail(message.toString());
    }

    /**
     * Asserts that the array of ints contains a given int
     *
     * @param expected expected int
     * @param array    the array
     */
    public static void assertContains(int expected, int[] array) {
        for (int anInt : array) {
            if (anInt == expected) {
                return;
            }
        }

        StringBuffer message = new StringBuffer();

        message.append(expected).append(" not in {");

        for (int i = 0; i < array.length; ++i) {
            message.append("'").append(array[i]).append("'");

            if (i < (array.length - 1)) {
                message.append(", ");
            }
        }

        message.append(" }");

        fail(message.toString());
    }

    /**
     * Asserts that the value of toString() on the given object matches the
     * given text string
     *
     * @param value    the object to be output to the console
     * @param expected the expected String representation
     */
    public static void assertToString(Object value, String expected) {
        Object console = InvokerHelper.invokeMethod(value, "toString", null);
        assertEquals("toString() on value: " + value, expected, console);
    }

    /**
     * Asserts that the value of inspect() on the given object matches the
     * given text string
     *
     * @param value    the object to be output to the console
     * @param expected the expected String representation
     */
    public static void assertInspect(Object value, String expected) {
        Object console = InvokerHelper.invokeMethod(value, "inspect", null);
        assertEquals("inspect() on value: " + value, expected, console);
    }

    /**
     * Asserts that the script runs without any exceptions
     *
     * @param script the script that should pass without any exception thrown
     */
    public static void assertScript(final String script) throws Exception {
        GroovyShell shell = new GroovyShell();
        shell.evaluate(script, genericScriptName());
    }

    /**
     * Asserts that the given code closure fails when it is evaluated
     *
     * @param code the code expected to fail
     * @return the caught exception
     */
    public static Throwable shouldFail(Closure code) {
        boolean failed = false;
        Throwable th = null;
        try {
            code.call();
        } catch (GroovyRuntimeException gre) {
            failed = true;
            th = ScriptBytecodeAdapter.unwrap(gre);
        } catch (Throwable e) {
            failed = true;
            th = e;
        }
        assertTrue("Closure " + code + " should have failed", failed);
        return th;
    }

    /**
     * Asserts that the given code closure fails when it is evaluated
     * and that a particular type of exception is thrown.
     *
     * @param clazz the class of the expected exception
     * @param code  the closure that should fail
     * @return the caught exception
     */
    public static Throwable shouldFail(Class clazz, Closure code) {
        Throwable th = null;
        try {
            code.call();
        } catch (GroovyRuntimeException gre) {
            th = ScriptBytecodeAdapter.unwrap(gre);
        } catch (Throwable e) {
            th = e;
        }

        if (th == null) {
            fail("Closure " + code + " should have failed with an exception of type " + clazz.getName());
        } else if (!clazz.isInstance(th)) {
            fail("Closure " + code + " should have failed with an exception of type " + clazz.getName() + ", instead got Exception " + th);
        }
        return th;
    }

    /**
     * Asserts that the given code closure fails when it is evaluated
     * and that a particular Exception type can be attributed to the cause.
     * The expected exception class is compared recursively with any nested
     * exceptions using getCause() until either a match is found or no more
     * nested exceptions exist.
     * <p>
     * If a match is found, the matching exception is returned
     * otherwise the method will fail.
     *
     * @param expectedCause the class of the expected exception
     * @param code          the closure that should fail
     * @return the cause
     */
    public static Throwable shouldFailWithCause(Class expectedCause, Closure code) {
        if (expectedCause == null) {
            fail("The expectedCause class cannot be null");
        }
        Throwable cause = null;
        Throwable orig = null;
        int level = 0;
        try {
            code.call();
        } catch (GroovyRuntimeException gre) {
            orig = ScriptBytecodeAdapter.unwrap(gre);
            cause = orig.getCause();
        } catch (Throwable e) {
            orig = e;
            cause = orig.getCause();
        }

        if (orig != null && cause == null) {
            fail("Closure " + code + " was expected to fail due to a nested cause of type " + expectedCause.getName() +
            " but instead got a direct exception of type " + orig.getClass().getName() + " with no nested cause(s). Code under test has a bug or perhaps you meant shouldFail?");
        }

        while (cause != null && !expectedCause.isInstance(cause) && cause != cause.getCause() && level < MAX_NESTED_EXCEPTIONS) {
            cause = cause.getCause();
            level++;
        }

        if (orig == null) {
            fail("Closure " + code + " should have failed with an exception having a nested cause of type " + expectedCause.getName());
        } else if (cause == null || !expectedCause.isInstance(cause)) {
            fail("Closure " + code + " should have failed with an exception having a nested cause of type " + expectedCause.getName() + ", instead found these Exceptions:\n" + buildExceptionList(orig));
        }
        return cause;
    }

    /**
     * Asserts that the given script fails when it is evaluated
     * and that a particular type of exception is thrown.
     *
     * @param clazz the class of the expected exception
     * @param script  the script that should fail
     * @return the caught exception
     */
    public static Throwable shouldFail(Class clazz, String script) {
        Throwable th = null;
        try {
            GroovyShell shell = new GroovyShell();
            shell.evaluate(script, genericScriptName());
        } catch (GroovyRuntimeException gre) {
            th = ScriptBytecodeAdapter.unwrap(gre);
        } catch (Throwable e) {
            th = e;
        }

        if (th == null) {
            fail("Script should have failed with an exception of type " + clazz.getName());
        } else if (!clazz.isInstance(th)) {
            fail("Script should have failed with an exception of type " + clazz.getName() + ", instead got Exception " + th);
        }
        return th;
    }

    /**
     * Asserts that the given script fails when it is evaluated
     *
     * @param script the script expected to fail
     * @return the caught exception
     */
    public static Throwable shouldFail(String script) {
        boolean failed = false;
        Throwable th = null;
        try {
            GroovyShell shell = new GroovyShell();
            shell.evaluate(script, genericScriptName());
        } catch (GroovyRuntimeException gre) {
            failed = true;
            th = ScriptBytecodeAdapter.unwrap(gre);
        } catch (Throwable e) {
            failed = true;
            th = e;
        }
        assertTrue("Script should have failed", failed);
        return th;
    }

    /**
     * Asserts that two objects are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * <code>expected</code> and <code>actual</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code> okay)
     * @param expected expected value
     * @param actual actual value
     */
    public static void assertEquals(String message, Object expected, Object actual) {

        if (expected == null && actual == null)
            return;

        if (expected != null && DefaultTypeTransformation.compareEqual(expected, actual))
            return;

        Assert.assertEquals(message, expected, actual);
    }

    /**
     * Asserts that two objects are equal. If they are not, an
     * {@link AssertionError} is thrown. If
     * <code>expected</code> and <code>actual</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param expected expected value
     * @param actual actual value
     */
    public static void assertEquals(Object expected, Object actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two Strings are equal. If they are not, an
     * {@link AssertionError} is thrown. If
     * <code>expected</code> and <code>actual</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param expected expected value
     * @param actual actual value
     */
    public static void assertEquals(String expected, String actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * NotYetImplemented Implementation
     */
    private static final ThreadLocal<Boolean> notYetImplementedFlag = new ThreadLocal<Boolean>();

    /**
     * From JUnit. Finds from the call stack the active running JUnit test case
     *
     * @return the test case method
     * @throws RuntimeException if no method could be found.
     */
    private static Method findRunningJUnitTestMethod(Class caller) {
        final Class[] args = new Class[]{};

        // search the initial junit test
        final Throwable t = new Exception();
        for (int i = t.getStackTrace().length - 1; i >= 0; --i) {
            final StackTraceElement element = t.getStackTrace()[i];
            if (element.getClassName().equals(caller.getName())) {
                try {
                    final Method m = caller.getMethod(element.getMethodName(), args);
                    if (isPublicTestMethod(m)) {
                        return m;
                    }
                }
                catch (final Exception e) {
                    // can't access, ignore it
                }
            }
        }
        throw new RuntimeException("No JUnit test case method found in call stack");
    }

    /**
     * From Junit. Test if the method is a JUnit 3 or 4 test.
     *
     * @param method the method
     * @return <code>true</code> if this is a junit test.
     */
    private static boolean isPublicTestMethod(final Method method) {
        final String name = method.getName();
        final Class[] parameters = method.getParameterTypes();
        final Class returnType = method.getReturnType();

        return parameters.length == 0
                && (name.startsWith("test") || method.getAnnotation(Test.class) != null)
                && returnType.equals(Void.TYPE)
                && Modifier.isPublic(method.getModifiers());
    }

    /**
     * <p>
     * Runs the calling JUnit test again and fails only if it unexpectedly runs.<br>
     * This is helpful for tests that don't currently work but should work one day,
     * when the tested functionality has been implemented.<br>
     * </p>
     *
     * <p>
     * The right way to use it for JUnit 3 is:
     *
     * <pre>
     * public void testXXX() {
     *   if (GroovyTestCase.notYetImplemented(this)) return;
     *   ... the real (now failing) unit test
     * }
     * </pre>
     *
     * or for JUnit 4
     *
     * <pre>
     * &#64;Test
     * public void XXX() {
     *   if (GroovyTestCase.notYetImplemented(this)) return;
     *   ... the real (now failing) unit test
     * }
     * </pre>
     * </p>
     *
     * <p>
     * Idea copied from HtmlUnit (many thanks to Marc Guillemot).
     * Future versions maybe available in the JUnit distribution.
     * </p>
     *
     * @return {@code false} when not itself already in the call stack
     */
    public static boolean notYetImplemented(Object caller) {
        if (notYetImplementedFlag.get() != null) {
            return false;
        }
        notYetImplementedFlag.set(Boolean.TRUE);

        final Method testMethod = findRunningJUnitTestMethod(caller.getClass());
        try {
            log.info("Running " + testMethod.getName() + " as not yet implemented");
            testMethod.invoke(caller, (Object[]) new Class[]{});
            fail(testMethod.getName() + " is marked as not yet implemented but passes unexpectedly");
        }
        catch (final Exception e) {
            log.info(testMethod.getName() + " fails which is expected as it is not yet implemented");
            // method execution failed, it is really "not yet implemented"
        }
        finally {
            notYetImplementedFlag.set(null);
        }
        return true;
    }

    private static String buildExceptionList(Throwable th) {
        StringBuilder sb = new StringBuilder();
        int level = 0;
        while (th != null) {
            if (level > 1) {
                for (int i = 0; i < level - 1; i++) sb.append("   ");
            }
            if (level > 0) sb.append("-> ");
            if (level > MAX_NESTED_EXCEPTIONS) {
                sb.append("...");
                break;
            }
            sb.append(th.getClass().getName()).append(": ").append(th.getMessage()).append("\n");
            if (th == th.getCause()) {
                break;
            }
            th = th.getCause();
            level++;
        }
        return sb.toString();
    }

}
