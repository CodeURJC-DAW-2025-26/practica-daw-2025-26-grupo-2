package es.dawgrupo2.zendashop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import es.dawgrupo2.zendashop.repository.GarmentRepository;
import es.dawgrupo2.zendashop.model.Garment;

@Controller
public class MainController {

    @Autowired
    private GarmentRepository garmentRepository;

    @GetMapping("/")
	public String index(Model model) {

        List<Garment> garments = garmentRepository.findAll();
        List<Garment> offers = garments; //De momento
        
        model.addAttribute("logged", false); //esto hay que cambiarlo para que sea una variable global la que diga el tipo de usuario que es
        model.addAttribute("admin", false);
        model.addAttribute("offers", offers);
        model.addAttribute("garments", garments);

        return "index"; // It charges src/main/resources/templates/index.html
    }

}
