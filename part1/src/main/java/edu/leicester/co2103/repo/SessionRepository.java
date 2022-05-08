package edu.leicester.co2103.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import edu.leicester.co2103.domain.Session;

public interface SessionRepository extends CrudRepository<Session, Long> {
	public List<Session> findAll();
}
