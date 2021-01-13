package com.example.demo.services;

import com.example.demo.models.Car;
import com.example.demo.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;

    public Iterable<Car> listAllCars() {
        return carRepository.findAll();
    }

    public Car getCarById(Integer id) {
        Optional<Car> oCar = carRepository.findById(id);
        return oCar.orElse(null);
    }

    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    public void deleteCar(Integer id) {
        Optional<Car> oCar = carRepository.findById(id);
        oCar.ifPresent(car -> carRepository.delete(car));
    }

    public Car add(Car car) {
        return carRepository.save(car);
    }

}
