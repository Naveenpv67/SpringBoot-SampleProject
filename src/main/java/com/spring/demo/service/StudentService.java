package com.spring.demo.service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.demo.entity.Student;
import com.spring.demo.model.CreateStudentRequest;
import com.spring.demo.model.StudentResponse;
import com.spring.demo.repo.StudentRepository;

@Service
public class StudentService {

	private StudentRepository studentRepository;
	
	private ModelMapper modelMapper;

	@Autowired
	public StudentService(StudentRepository studentRepository, ModelMapper modelMapper) {
		
		if(studentRepository == null) {
			throw new NullPointerException("Dependencies cant be null");
		}
		this.studentRepository = studentRepository;
		this.modelMapper = modelMapper;
	}
	
	public StudentResponse createStudent(CreateStudentRequest req) {
	
		Student student = modelMapper.map(req, Student.class);
		Student savedStudent = studentRepository.save(student);
		StudentResponse studentResponse = modelMapper.map(savedStudent, StudentResponse.class);
		
		return studentResponse;
	}
	
	public List<StudentResponse> getAllStudents(){
		
		List<Student> list = studentRepository.findAll();
		Type type = new TypeToken<List<StudentResponse>>() {}.getType();
		List<StudentResponse> response = modelMapper.map(list, type);
		
		return response;
	}

	public StudentResponse getStudentById(Long id) {

		 StudentResponse studentResponse = null;
		Optional<Student> stud = studentRepository.findById(id);
		if(stud.isPresent()) {
		 Student student = stud.get();
		 studentResponse = modelMapper.map(student, StudentResponse.class);
		}
		return studentResponse;
	}
	
}
