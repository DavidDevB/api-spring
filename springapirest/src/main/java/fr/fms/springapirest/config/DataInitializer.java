package fr.fms.springapirest.config;

import fr.fms.springapirest.entities.Training;
import fr.fms.springapirest.service.TrainingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

  private final TrainingService trainingService;

  public DataInitializer(TrainingService trainingService) {
    this.trainingService = trainingService;
  }

  @Override
  public void run(String... args) throws Exception {
    // Initialize data here

    if (trainingService.findAll().isEmpty()) {
      trainingService.create(new Training(null, "Java Basics", "Learn the basics of Java programming.", 199.99, 10));
      trainingService.create(new Training(null, "Spring Boot", "Build RESTful APIs with Spring Boot.", 299.99, 15));
      trainingService.create(
        new Training(null, "React for Beginners", "Introduction to React.js for front-end development.", 149.99, 20)
      );
    }
  }
}
