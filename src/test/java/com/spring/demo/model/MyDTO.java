package com.example.aerospikedemo.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class MyDTO implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String primaryKey;
    private String field1;
    private String field2;
}
