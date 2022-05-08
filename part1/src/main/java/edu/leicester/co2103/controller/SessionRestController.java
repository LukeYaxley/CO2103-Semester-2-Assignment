package edu.leicester.co2103.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.leicester.co2103.domain.Session;
import edu.leicester.co2103.repo.ConvenorRepository;
import edu.leicester.co2103.repo.ModuleRepository;
import edu.leicester.co2103.repo.SessionRepository;

@RestController
@RequestMapping("/")
public class SessionRestController {
	
	@Autowired
	ConvenorRepository convenorrepo;
	@Autowired
	ModuleRepository modulerepo;
	@Autowired
	SessionRepository sessionrepo;
	
	//Endpoint 18
	@DeleteMapping("/sessions")
	public ResponseEntity<?> deleteSessions() {
		List<Session> sessions = sessionrepo.findAll();
		sessionrepo.deleteAll();
		return ResponseEntity.ok(null);
	}
	
	//Endpoint 19-20
	@GetMapping("/sessions")
	public ResponseEntity<?> getSessions(@RequestParam(required=false) Long convenor, @RequestParam(required=false) String module) {
		
		List<Session> sessions = new ArrayList<Session>();
		try {	
		// Puts all sessions with no filter
		if (convenor == null && module == null) {
			sessions = sessionrepo.findAll();
		}
		
		// no module
		if (convenor != null && module == null) {
			for(int i=0; i<convenorrepo.findById(convenor).get().getModules().size(); i++) {
				sessions.addAll(convenorrepo.findById(convenor).get().getModules().get(i).getSessions());
			}
		}
		
		// no convenor
		if (convenor == null && module != null) {
			sessions = modulerepo.findById(module).get().getSessions();
		}
		
		
		//Error cases
		// Checks to see if convenor exists
		if (convenor != null && convenorrepo.findById(convenor).isEmpty()){
				return new ResponseEntity<ErrorInfo>(new ErrorInfo("Convenor does not exist."), HttpStatus.NOT_FOUND);
		} 
				
		// module does not exist
		if (module != null && modulerepo.findById(module).isEmpty()){
				return new ResponseEntity<ErrorInfo>(new ErrorInfo("Module does not exist."), HttpStatus.NOT_FOUND);
		}
		// both querys
		if (convenor != null && module != null) {
			if (convenorrepo.findById(convenor).get().getModules().contains(modulerepo.findById(module).get())) {
				sessions = modulerepo.findById(module).get().getSessions();
			} else
				return new ResponseEntity<ErrorInfo>(new ErrorInfo("Module does not belong to Convenor."), HttpStatus.NOT_FOUND);
		}
		
		
		// no sessions at the end
				
			return new ResponseEntity<List<Session>>(sessions, HttpStatus.OK);

		} catch(NoSuchElementException e) {
			return new ResponseEntity<List<Session>>(HttpStatus.NO_CONTENT);

	}}
	
}
