package fr.fms.springapirest.controller;

import fr.fms.springapirest.entities.Training;
import fr.fms.springapirest.service.TrainingService;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

  private final TrainingService trainingService;

  public TrainingController(TrainingService trainingService) {
    this.trainingService = trainingService;
  }

  @GetMapping
  public List<Training> findAll() {
    return trainingService.findAll();
  }

  @GetMapping("/{id}")
  public Training findOne(@PathVariable Long id) {
    return trainingService.findById(id).isPresent() ? trainingService.findById(id).get() : null;
  }

  @PostMapping
  public Training create(@RequestBody Training training) {
    return trainingService.create(training);
  }

  @PutMapping("/{id}")
  public Training update(@PathVariable Long id, @RequestBody Training training) {
    Optional<Training> updatedTraining = trainingService.update(id, training);
    return updatedTraining.isPresent() ? updatedTraining.get() : null;
  }

  @DeleteMapping("/{id}")
  public boolean delete(@PathVariable Long id) {
    return trainingService.deleteById(id);
  }
}
