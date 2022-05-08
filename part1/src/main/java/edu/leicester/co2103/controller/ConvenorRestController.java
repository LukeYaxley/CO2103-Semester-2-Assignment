package edu.leicester.co2103.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.leicester.co2103.domain.Convenor;
import edu.leicester.co2103.repo.ConvenorRepository;

@RestController
@RequestMapping("/")
public class ConvenorRestController {
	public static final Logger logger = LoggerFactory.getLogger(ConvenorRestController.class);

	@Autowired
	ConvenorRepository repo;
	
	//Endpoint 1
	@GetMapping("/convenors")
	public ResponseEntity<?> listConvenors(){
		if (repo.findAll().isEmpty()) {
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("No Convenors in University"),HttpStatus.NO_CONTENT);
		}
		List<Convenor> convenors = (List<Convenor>) repo.findAll();
		return new ResponseEntity<List<Convenor>>(convenors, HttpStatus.OK);
	}
	
	//Endpoint 2
	@PostMapping("/convenors")
	public ResponseEntity<?> createConvenor(@RequestBody Convenor convenor, UriComponentsBuilder ucBuilder) {
		if(repo.existsById(convenor.getId())) {
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("A convenor with given id already exists. "), HttpStatus.CONFLICT);
		}
		repo.save(convenor);
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.setLocation(ucBuilder.path("/convenors/{id}").buildAndExpand(convenor.getId()).toUri());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}
	//Endpoint 3
	@GetMapping("/convenors/{id}")
	public ResponseEntity<?> getConvenorById(@PathVariable("id") long id) {
		
		try {
			Convenor convenor = repo.findById(id).get();
			return new ResponseEntity<Convenor>(convenor, HttpStatus.OK);
		}
		catch(NoSuchElementException e) {
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Convenor with given id not found."),HttpStatus.NOT_FOUND);
		}
	
	}
	//Endpoint 6
	@GetMapping("/convenors/{id}/modules")
	public ResponseEntity<?> getModulesByConvenor(@PathVariable("id") Long id) {
		
		if (repo.findById(id).isPresent()) {
			List<edu.leicester.co2103.domain.Module> modules = repo.findById(id).get().getModules();
			if (modules.isEmpty()) {
				return new ResponseEntity<List<edu.leicester.co2103.domain.Module>>(HttpStatus.NO_CONTENT);
			} else 
			return new ResponseEntity<List<edu.leicester.co2103.domain.Module>>(modules, HttpStatus.OK);
		
		} else
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Convenor not found."), HttpStatus.NOT_FOUND);

	}
	//Endpoint 5
	@DeleteMapping("/convenors/{id}")
	public ResponseEntity<?> deleteConvenor(@PathVariable("id") Long id) {
		
		if (repo.findById(id).isPresent()) {
			repo.deleteById(id);
			return ResponseEntity.ok(null);
		} else
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Convenor not found."), HttpStatus.NOT_FOUND);

	}
	//Endpoint 4
	@PutMapping("/convenors/{id}")
	public ResponseEntity<?> updateConvenor(@PathVariable("id") Long id, @RequestBody Convenor newConvenor) {

		if (repo.findById(id).isPresent()) {
			Convenor current = repo.findById(id).get();
			current.setPosition(newConvenor.getPosition());
			current.setName(newConvenor.getName());			
			current.getModules().clear();
			current.getModules().addAll(newConvenor.getModules());

			repo.save(current);
			return new ResponseEntity<Convenor>(current, HttpStatus.OK);
		} else
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Convenor not found."),
					HttpStatus.NOT_FOUND);
	}
}
