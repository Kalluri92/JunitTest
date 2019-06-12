package com.example.demo;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class SampleControllerTest {
	 	
		private MockMvc mockMvc;
		
		@InjectMocks
	    private  SampleController sampleController;
		
		@Mock
		private TokenGenerationService tokenGenerationSevice;


	    @Before
	    public void init(){
	        MockitoAnnotations.initMocks(this);
	        mockMvc = MockMvcBuilders
	                .standaloneSetup(sampleController)
	                .build();
	        when(tokenGenerationSevice.getCobrandJWT()).thenReturn("ABCDEF");
	    
	    }
	    
	    @Test
	    public void testResturnType() throws Exception {
	    	 mockMvc.perform(get("/"))
	            .andExpect(status().isOk()).andExpect(content().string(new String("ABCDEF")));
	    }
}
