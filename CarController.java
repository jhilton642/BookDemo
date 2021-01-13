package com.example.demo.controllers;

import com.example.demo.models.Car;
import com.example.demo.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/cars")            //  this will add cars to the start of all URL endpoints
public class CarController {

    @Autowired
    //    @Autowired will request SpringBoot to find the CarService class and instantiate one for us
    //    and assign (INJECT) the class property with the value. This is Dependency Injection.
    //    our class depends on this service and SpringBoot will inject it into our class
    private CarService carService;

    /**
     *         Environment     Provides access the the application.properties file. The getProperty method will retrieve
     *                         the value of a property which we can use in the code. For instance in this Controller
     *                         we are interested in the destination folder for the images we will be uploading. We save
     *                         the folder in the properties files. This property is consistent and available to any
     *                         code that wishes to use these same folders
     */
    @Autowired
    private Environment environment;

    @RequestMapping("/")                                    //  this code will be reached by /cars/
    public String index(Model model) {
        //  get a list of all cars add to the model and list them
        Iterable<Car> cars = carService.listAllCars();
        model.addAttribute("cars", cars);

        //  the the carList page will be happy to display it
        return "carList";
    }

    //  let's CREATE a new car
    @RequestMapping("/new")
    public String newCar(Model model){
        //  since we do not have a car, let's send an empty car to the carEdit page
        model.addAttribute("car", new Car());
        return "carEdit";
    }

    //  id will be the key to the car we want to READ from the database
    @RequestMapping("/{id}")
    public String readCar(@PathVariable Integer id, Model model){
        //  find in the database a car with id = to our PathVariable
        Car car = carService.getCarById(id);

        //  did we find a car?
        if ( car != null ) {
            //  yes. add the car to the model and display the carDetails page
            model.addAttribute("car", car);
            return "carDetails";
        }
        else {
            //  no, we did not find a car. Display an error message
            model.addAttribute("message", "The Car Id: " + id + " was not found in the database");
            return "404";       //  car (page) not found
        }
    }

    //  id will be the key to the car we want to UPDATE
    @RequestMapping("/edit/{id}")
    public String updateCar(@PathVariable Integer id, Model model){
        //  find the car in the database and send that data to the carEdit page
        model.addAttribute("car", carService.getCarById(id));
        return "carEdit";
    }

    //  we have finished making our changes to our car. The data is POSTed back to the server
    //  all of the data is saved in a Car object and UPDATEd in the database.
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveCar(Car car, MultipartFile file){
        //    find where we need to save the file on the server. application.properties has an property call eta.uploadFolder
        //    it is defined with the folder destination for our upload files 
        String uploadFolder = environment.getProperty("eta.uploadFolder");
        
        //    load the file and give us back the location of the image to include that in our Car record in the database
        if (! file.isEmpty()) {
            String fileName = uploadFile(file, uploadFolder, "images");
            car.setImageUrl(fileName);                                    //    update imageUrl property with our image
        }
        //  all we have to do is save the car
        carService.saveCar(car);
        //  go to the list all cars page when complete
        return "redirect:/cars/";
    }

    //  using the id from the URL find and DELETE our car
    @RequestMapping("/delete/{id}")
    public String deleteCar(@PathVariable Integer id){
        carService.deleteCar(id);
        //  go to the list all cars page when complete
        return "redirect:/cars/";
    }

/*
    //  using the whatever from the search form get all cars by this whatever
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String searchCar(@RequestParam String Whatever, Model model ){
        //  SEARCH for all cars by Whatever
        Iterable<Car> list = carService.findByWhatever(Whatever);

        //  pass the list of cars by Whatever
        model.addAttribute("Whatever", Whatever);
        model.addAttribute("cars", list);

        //  the the carList page will be happy to display it
        return "carList";
    }
*/
    /**
     *        uploadFile
     * @param file                the file from the browser
     * @param uploadFolder        the folder to save the file to
     * @param subfolder           the particular subfolder for the file type
     * @return                    the name of the file to be saved to the database
     */
    public static String uploadFile(MultipartFile file, String uploadFolder, String subfolder) {
        String fileName = null;
        try {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();              //    read the entire file into this buffer
            fileName = file.getOriginalFilename();       //    get the name of the file being uploaded

            Path path = Paths.get(".");                //    what is the current directory?
            //    build a path to the upload folder
            path = Paths.get(path.toAbsolutePath() + uploadFolder + subfolder + "/" + fileName);
            Files.write(path, bytes);                    //    save the file to the upload folder
        } catch (IOException e) {
            e.printStackTrace();                         //    just in case things go bad
        }
        return "/" + subfolder + "/" + fileName;     //    return the 'relative' location of the file
    }
}
