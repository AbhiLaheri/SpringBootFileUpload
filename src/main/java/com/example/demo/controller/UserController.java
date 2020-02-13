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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.example.demo.Domain.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Security.AuthenticationRequest;
import com.example.demo.Security.AuthenticationResponse;
import com.example.demo.Security.JwtUtil;
import com.example.demo.Security.MyUserDetailsService;
import com.example.demo.Service.FileService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Autowired
	private MyUserDetailsService userDetailsService;

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

	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
					);
		}
		catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password", e);
		}


		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());

		final String jwt = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}

}
