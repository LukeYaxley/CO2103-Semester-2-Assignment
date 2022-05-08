package edu.leicester.co2103.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import edu.leicester.co2103.domain.Convenor;

public interface ConvenorRepository extends CrudRepository<Convenor, Long> {
	public List<Convenor> findAll();
}
