/*
 * Copyright 2003-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class MockingExampleTests extends GroovyTestCase {

    // tag::map_coercion[]
    static class TranslationService {
        String convert(String key) {
            return "test"
        }
    }
    // end::map_coercion[]

    void testAssertions() {
        // tag::map_coercion[]
        def service = [convert: { String key -> 'some text' }] as TranslationService
        assert 'some text' == service.convert('key.text')
        // end::map_coercion[]
    }
}
