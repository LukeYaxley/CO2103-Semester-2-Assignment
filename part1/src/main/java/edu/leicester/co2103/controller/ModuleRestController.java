package edu.leicester.co2103.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.leicester.co2103.domain.Session;
import edu.leicester.co2103.repo.ConvenorRepository;
import edu.leicester.co2103.repo.ModuleRepository;
import edu.leicester.co2103.repo.SessionRepository;
import edu.leicester.co2103.domain.Convenor;
import edu.leicester.co2103.domain.Module;
@RestController
@RequestMapping("/")
public class ModuleRestController {
	@Autowired
	SessionRepository sessionrepo;
	@Autowired
	ConvenorRepository convenorrepo;
	@Autowired
	ModuleRepository modulerepo;
	
	//Endpoint 7
	@GetMapping("/modules")
	public ResponseEntity<List<edu.leicester.co2103.domain.Module>> listModules() {
		List<edu.leicester.co2103.domain.Module> modules =modulerepo.findAll();

		try {
		return new ResponseEntity<List<edu.leicester.co2103.domain.Module>>(modules, HttpStatus.OK);
		}catch(ArrayIndexOutOfBoundsException e) {
			return new ResponseEntity<List<edu.leicester.co2103.domain.Module>>(HttpStatus.NO_CONTENT);
		}
	}
	//Endpoint 9
	@GetMapping("/modules/{code}")
	public ResponseEntity<?> getModule(@PathVariable("code") String code) {
		
		try {
			edu.leicester.co2103.domain.Module module = modulerepo.findById(code).get();
			return new ResponseEntity<edu.leicester.co2103.domain.Module>(module, HttpStatus.OK);
		} catch(NoSuchElementException e) {
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Module not found"),
					HttpStatus.NOT_FOUND);
		}
	}
	
	//Endpoint 12
	@GetMapping("/modules/{code}/sessions")
	public ResponseEntity<?> getSessionsByModule(@PathVariable("code") String code) {
		
		try {
			List<Session> sessions = modulerepo.findById(code).get().getSessions();
			if (sessions.isEmpty()) {
				return new ResponseEntity<List<Session>>(HttpStatus.NO_CONTENT);
			} else 
			return new ResponseEntity<List<Session>>(sessions, HttpStatus.OK);
		
		} catch(NoSuchElementException e) {
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Module not found."), HttpStatus.NOT_FOUND);
		}
	}
	//Endpoint 14
	@GetMapping("/modules/{code}/sessions/{id}")
	public ResponseEntity<?> getModuleSession(@PathVariable("code") String code, @PathVariable("id") long id) {
		if (modulerepo.findById(code).isPresent()) {
			if (sessionrepo.findById(id).isPresent() && modulerepo.findById(code).get().getSessions().contains(sessionrepo.findById(id).get())) {
				return new ResponseEntity<Session>(sessionrepo.findById(id).get(), HttpStatus.OK);
			} else
				return new ResponseEntity<ErrorInfo>(new ErrorInfo("Session not found."), HttpStatus.NOT_FOUND);
		} else
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Module not found."), HttpStatus.NOT_FOUND);
	}
	//Endpoint 8
	@PostMapping("/modules")
	public ResponseEntity<?> createModule(@RequestBody Module module, UriComponentsBuilder uBuilder) {
		if(modulerepo.existsById(module.getCode())) {
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("module already exists. "), HttpStatus.CONFLICT);
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uBuilder.path("/modules/{id}").buildAndExpand(module.getCode()).toUri());
		modulerepo.save(module);
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}
	//Endpoint 10
	@PatchMapping("/modules/{code}")
	public ResponseEntity<?> editModule(@PathVariable("code") String code, @RequestBody Module newMod) {
		
		try{
			Module current = modulerepo.findById(code).get();
			current = newMod;
			
			modulerepo.save(current);
			return new ResponseEntity<Module>(current, HttpStatus.OK);
		} catch(NoSuchElementException e) {
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Module not found."),
					HttpStatus.NOT_FOUND);
	}}
	//Endpoint 16
	@PatchMapping("/modules/{code}/sessions/{id}")
	public ResponseEntity<?> updateModuleSession(@PathVariable("code") String modCode, @PathVariable("id") long id, @RequestBody Session newSession) {
		try {
			Session currentSession = sessionrepo.findById(id).get();
			currentSession = newSession;

			sessionrepo.save(currentSession);
			
			return new ResponseEntity<Session>(currentSession, HttpStatus.OK);
			} catch(NoSuchElementException e) {
				return new ResponseEntity<ErrorInfo>(new ErrorInfo("Session not found. either session id or module code doesn't match"), HttpStatus.NOT_FOUND);

		} 
	}
	//Endpoint 11
	@DeleteMapping("/modules/{code}")
	public ResponseEntity<?> deleteModule(@PathVariable("code") String code) {
		if (modulerepo.findById(code).isPresent()) {
			Module Remove = modulerepo.findById(code).get();
			//Searches all through convenors and removes references to records
			List<Convenor> convenors = convenorrepo.findAll();
			for(int i=0; i < convenors.size(); i++) {
				if (convenors.get(i).getModules().contains(Remove)) {
					convenors.get(i).getModules().remove(Remove);
					convenorrepo.save(convenors.get(i));
				}
			}
			
			modulerepo.deleteById(code);
			return ResponseEntity.ok(null);
		} else
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Module not found."), HttpStatus.NOT_FOUND);

	}
	//Endpoint 17
	@DeleteMapping("/modules/{code}/sessions/{id}")
	public ResponseEntity<?> deleteModuleSession(@PathVariable("code") String code, @PathVariable("id") long id) {
		if (modulerepo.findById(code).isPresent()) {
			if (sessionrepo.findById(id).isPresent() && modulerepo.findById(code).get().getSessions().contains(sessionrepo.findById(id).get())) {
				modulerepo.findById(code).get().getSessions().remove(sessionrepo.findById(id).get());
				sessionrepo.deleteById(id);
				return ResponseEntity.ok(null);
			} else
				return new ResponseEntity<ErrorInfo>(new ErrorInfo("Session not found."), HttpStatus.NOT_FOUND);

		} else
			return new ResponseEntity<ErrorInfo>(new ErrorInfo("Module not found."), HttpStatus.NOT_FOUND);
	}
	
}
