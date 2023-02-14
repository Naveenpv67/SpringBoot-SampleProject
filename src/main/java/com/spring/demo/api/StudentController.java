package com.spring.demo.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.demo.model.CreateStudentRequest;
import com.spring.demo.model.StudentResponse;
import com.spring.demo.service.StudentService;

@RestController
@RequestMapping("/student")
public class StudentController {
	
	@Autowired
	private StudentService studentService;

	@PostMapping("/create")
	public StudentResponse saveStudent(@RequestBody CreateStudentRequest req) {
		
		return studentService.createStudent(req);
	}
	
	@GetMapping()
	public List<StudentResponse> getAll() {
		
		return studentService.getAllStudents();
	}
	
	@GetMapping("/{studentId}")
	public StudentResponse getStudentById(@PathVariable("studentId") Long id) {
		
		return studentService.getStudentById(id);
	}
	
}
