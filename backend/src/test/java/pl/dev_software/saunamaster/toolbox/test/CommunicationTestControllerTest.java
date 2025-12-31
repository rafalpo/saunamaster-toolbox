package pl.dev_software.saunamaster.toolbox.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class CommunicationTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test() throws Exception {
        mockMvc.perform(get("/test"))
            .andExpect(status().isOk())
            .andExpect(content().json("{\"message\": \"Hello World!\"}"));
    }

}