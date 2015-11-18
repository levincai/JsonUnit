/**
 * Copyright 2009-2015 the original author or authors.
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
package net.javacrumbs.jsonunit.spring;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringConfig.class})
@WebAppConfiguration
public class ExampleControllerTest {

    public static final String CORRECT_JSON = "{\"result\":{\"string\":\"stringValue\", \"array\":[1, 2, 3]}}";
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void shouldPassIfEquals() throws Exception {
        exec().andExpect(json().isEqualTo(CORRECT_JSON));
    }

    @Test
    public void isEqualToShouldFailIfDoesNotEqual() throws Exception {
        try {
            exec()
                .andExpect(json().isEqualTo(CORRECT_JSON.replace("stringValue", "stringValue2")));
            doFail();
        } catch (AssertionError e) {
            assertEquals(
                "JSON documents are different:\n" +
                    "Different value found in node \"result.string\". Expected \"stringValue2\", got \"stringValue\".\n",
                e.getMessage());
        }
    }

    @Test
    public void isAbsentShouldFailIfNodeExists() throws Exception {
        try {
            exec()
                .andExpect(json().node("result.string").isAbsent());
            doFail();
        } catch (AssertionError e) {
            assertEquals("Node \"result.string\" is present.", e.getMessage());
        }
    }


    @Test
    public void isAbsentShouldPassIfNodeIsAbsent() throws Exception {
        exec().andExpect(json().node("result.string2").isAbsent());
    }

    @Test
    public void isPresentShouldFailIfNodeIsAbsent() throws Exception {
        try {
            exec()
                .andExpect(json().node("result.string2").isPresent());
            doFail();
        } catch (AssertionError e) {
            assertEquals("Node \"result.string2\" is missing.", e.getMessage());
        }
    }

    @Test
    public void isPresentShouldPassIfPresent() throws Exception {
        exec().andExpect(json().node("result.string").isPresent());
    }

    @Test
    public void isArrayShouldFailOnNotArray() throws Exception {
        try {
            exec()
                .andExpect(json().node("result.string").isArray());
            doFail();
        } catch (AssertionError e) {
            assertEquals("Node \"result.string\" is not an array. The actual value is '\"stringValue\"'.", e.getMessage());
        }
    }

    @Test
    public void isArrayShouldPassOnArray() throws Exception {
        exec().andExpect(json().node("result.array").isArray());
    }

    @Test
    public void isNotEqualToShouldFailIfEquals() throws Exception {
        try {
            exec()
                .andExpect(json().isNotEqualTo(CORRECT_JSON));
            doFail();
        } catch (AssertionError e) {
            assertEquals(
                "JSON is equal.", e.getMessage());
        }
    }

    @Test
    public void isEqualToShouldFailIfNodeDoesNotEqual() throws Exception {
        try {
            exec()
                .andExpect(json().node("result.string").isEqualTo("stringValue2"));
            doFail();
        } catch (AssertionError e) {
            assertEquals("JSON documents are different:\n" +
                    "Different value found in node \"result.string\". Expected \"stringValue2\", got \"stringValue\".\n",
                e.getMessage());
        }
    }

    private ResultActions exec() throws Exception {
        return this.mockMvc.perform(get("/sample").accept(MediaType.APPLICATION_JSON));
    }

    private void doFail() {
        fail("Exception expected");
    }

}
