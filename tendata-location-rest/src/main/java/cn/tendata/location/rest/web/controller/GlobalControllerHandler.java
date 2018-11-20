package cn.tendata.location.rest.web.controller;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class GlobalControllerHandler {
    
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		/* Converts empty strings into null when a form is submitted */  
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
}
