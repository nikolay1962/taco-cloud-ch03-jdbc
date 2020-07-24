package com.nisha.tacocloud.controller;

import com.nisha.tacocloud.domain.Ingredient;
import com.nisha.tacocloud.domain.Ingredient.Type;
import com.nisha.tacocloud.domain.Order;
import com.nisha.tacocloud.domain.Taco;
import com.nisha.tacocloud.repository.IngredientRepository;
import com.nisha.tacocloud.repository.TacoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {

    private final IngredientRepository ingredientRepository;
    private final TacoRepository tacoRepository;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepository,
                                TacoRepository tacoRepository) {
        this.ingredientRepository = ingredientRepository;
        this.tacoRepository = tacoRepository;
    }

    @ModelAttribute(name = "order")
    public Order order() {
        return new Order();
    }

    @ModelAttribute(name = "taco")
    public Taco design() {
        return new Taco();
    }

    @GetMapping
    public String showDesignForm(Model model) {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredientRepository.findAll().forEach(ingredient -> ingredients.add(ingredient));
        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
        }
//        model.addAttribute("taco", new Taco());
        return "design";
    }

    @PostMapping
    public String processDesign(@Valid Taco taco,
                                Errors errors,
                                @ModelAttribute Order order) {
        log.info("Processing design: " + taco);
        log.info("Errors: " + errors);
        log.info("@ModelAttribute Order order: " + order);

        if (errors.hasErrors()) {
            return "design";
        }

        Taco savedTaco = tacoRepository.save(taco);
        order.addDesign(savedTaco);
        // We'll do this in chapter 3

        return "redirect:/orders/current";
    }

    private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {

        return ingredients.stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());

    }
}
