package com.example.demo.controller;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.example.demo.Domain.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.FileService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
	private final Logger log = LoggerFactory.getLogger(UserController.class);
	private final FileService fileService;

	public UserController(FileService fileService) {
		this.fileService = fileService;
	}

	@Autowired
	private  UserRepository userRepository;

	@RequestMapping("/show")
	public List<User> index() {
		return (List<User>)userRepository.findAll();
	}

	@RequestMapping("/")
	public String show() {
		return "Greetings from Abhishek!";
	}

	@PostMapping("/upload")
	public String uploadFile(
			StandardMultipartHttpServletRequest request) {
		//log.info("REST request to create customer and user");
		MultipartFile file = null;
		Iterator<String> parameterNames = request.getFileNames();
		if (parameterNames.hasNext()) {
			String paramName = parameterNames.next();
			file = request.getFile(paramName);
		}
		fileService.saveFile(file);	
      return "File Upload Successfully";
	}
	@GetMapping("/fileuploadtest")
	public String fileUploadTest() {
		try {
			fileService.getContainer();
		} catch (InvalidKeyException | URISyntaxException e) {
			// TODO Auto-generated catch block
			log.error("Exception"+e.getMessage());
			
		}
		return "Greetings from Abhishek!";
	}



}
