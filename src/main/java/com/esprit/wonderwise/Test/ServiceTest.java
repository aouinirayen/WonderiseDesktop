package com.esprit.wonderwise.Test;

import com.esprit.wonderwise.Model.*;
import com.esprit.wonderwise.Service.*;

import java.sql.Date;
import java.util.List;

public class ServiceTest {
    public static void main(String[] args) {
        // Initialize all services
        CountryService countryService = new CountryService();
        CelebrityService celebrityService = new CelebrityService();
        ArtService artService = new ArtService();
        MonumentService monumentService = new MonumentService();
        TraditionalFoodService foodService = new TraditionalFoodService();

        // === Test CountryService CRUD ===
        System.out.println("=== Testing CountryService CRUD ===");
        // Create
        Country country = new Country(
                0, // ID will be auto-generated
                "Italy",
                "italy.jpg",
                "A country known for its history, art, and cuisine.",
                "EUR",
                "IT",
                "+39",
                "Mediterranean"
        );
        countryService.add(country);
        System.out.println("Created Country: " + country.getName());

        // Read
        List<Country> countries = countryService.readAll();
        if (countries.isEmpty()) {
            System.out.println("No countries found. Exiting test.");
            return;
        }
        Country addedCountry = countries.get(0); // Latest country (ORDER BY id DESC)
        int countryId = addedCountry.getId();
        System.out.println("Read Country: " + addedCountry);

        // Update
        addedCountry.setDescription("A country famous for its history, art, cuisine, and fashion.");
        countryService.update(addedCountry);
        System.out.println("Updated Country: " + countryService.getById(countryId));

        // === Test CelebrityService CRUD ===
        System.out.println("\n=== Testing CelebrityService CRUD ===");
        // Create
        Celebrity celebrity = new Celebrity(
                0, // ID will be auto-generated
                countryId,
                "Monica Bellucci",
                "Actress",
                "monica_bellucci.jpg",
                "A renowned Italian actress and model.",
                "Actress",
                Date.valueOf("1964-09-30"),
                "Italian",
                "Malèna, The Matrix Reloaded",
                "Married to Vincent Cassel (1999-2013)",
                45000000.0
        );
        celebrityService.add(celebrity);
        System.out.println("Created Celebrity: " + celebrity.getName());

        // Read
        List<Celebrity> celebrities = celebrityService.readByCountryId(countryId);
        if (celebrities.isEmpty()) {
            System.out.println("No celebrities found for country ID: " + countryId);
        } else {
            Celebrity addedCelebrity = celebrities.get(0); // Latest celebrity
            int celebrityId = addedCelebrity.getId();
            System.out.println("Read Celebrities for Italy: " + celebrities);

            // Update
            addedCelebrity.setNotableWorks("Malèna, The Matrix Reloaded, Spectre");
            celebrityService.update(addedCelebrity);
            System.out.println("Updated Celebrity: " + celebrityService.getById(celebrityId));

            // Delete
            celebrityService.delete(celebrityId);
            System.out.println("Deleted Celebrity ID: " + celebrityId);
            System.out.println("Celebrities after deletion: " + celebrityService.readByCountryId(countryId));
        }

        // === Test ArtService CRUD ===
        System.out.println("\n=== Testing ArtService CRUD ===");
        // Create
        Art art = new Art(
                0, // ID will be auto-generated
                countryId,
                "The Last Supper",
                "last_supper.jpg",
                "A famous mural painting by Leonardo da Vinci.",
                "1498-01-01", // Use a valid date format (YYYY-MM-DD)
                "Mural"
        );
        artService.add(art);
        System.out.println("Created Art: " + art.getName());

        // Read
        List<Art> arts = artService.readByCountryId(countryId);
        if (arts.isEmpty()) {
            System.out.println("No art found for country ID: " + countryId);
        } else {
            Art addedArt = arts.get(0); // Latest art
            int artId = addedArt.getId();
            System.out.println("Read Art for Italy: " + arts);

            // Update
            addedArt.setDescription("A famous mural painting by Leonardo da Vinci, depicting the Last Supper of Jesus.");
            artService.update(addedArt);
            System.out.println("Updated Art: " + artService.getById(artId));

            // Delete
            artService.delete(artId);
            System.out.println("Deleted Art ID: " + artId);
            System.out.println("Art after deletion: " + artService.readByCountryId(countryId));
        }

        // === Test MonumentService CRUD ===
        System.out.println("\n=== Testing MonumentService CRUD ===");
        // Create
        Monument monument = new Monument(
                0, // ID will be auto-generated
                countryId,
                "Colosseum",
                "colosseum.jpg",
                "An ancient amphitheater in Rome, used for gladiatorial contests."
        );
        monumentService.add(monument);
        System.out.println("Created Monument: " + monument.getName());

        // Read
        List<Monument> monuments = monumentService.readByCountryId(countryId);
        if (monuments.isEmpty()) {
            System.out.println("No monuments found for country ID: " + countryId);
        } else {
            Monument addedMonument = monuments.get(0); // Latest monument
            int monumentId = addedMonument.getId();
            System.out.println("Read Monuments for Italy: " + monuments);

            // Update
            addedMonument.setDescription("An ancient amphitheater in Rome, a symbol of Roman engineering.");
            monumentService.update(addedMonument);
            System.out.println("Updated Monument: " + monumentService.getById(monumentId));

            // Delete
            monumentService.delete(monumentId);
            System.out.println("Deleted Monument ID: " + monumentId);
            System.out.println("Monuments after deletion: " + monumentService.readByCountryId(countryId));
        }

        // === Test TraditionalFoodService CRUD ===
        System.out.println("\n=== Testing TraditionalFoodService CRUD ===");
        // Create
        TraditionalFood food = new TraditionalFood(
                0, // ID will be auto-generated
                countryId,
                "Pizza Margherita",
                "pizza_margherita.jpg",
                "A traditional Italian pizza with tomato, mozzarella, and basil.",
                "Mix flour, yeast, water for dough; add tomato, mozzarella, basil; bake at 250°C."
        );
        foodService.add(food);
        System.out.println("Created Traditional Food: " + food.getName());

        // Read
        List<TraditionalFood> foods = foodService.readByCountryId(countryId);
        if (foods.isEmpty()) {
            System.out.println("No traditional foods found for country ID: " + countryId);
        } else {
            TraditionalFood addedFood = foods.get(0); // Latest food
            int foodId = addedFood.getId();
            System.out.println("Read Traditional Foods for Italy: " + foods);

            // Update
            addedFood.setRecipe("Mix flour, yeast, water for dough; add fresh tomato, mozzarella, basil; bake at 250°C for 10 minutes.");
            foodService.update(addedFood);
            System.out.println("Updated Traditional Food: " + foodService.getById(foodId));

            // Delete
            foodService.delete(foodId);
            System.out.println("Deleted Traditional Food ID: " + foodId);
            System.out.println("Traditional Foods after deletion: " + foodService.readByCountryId(countryId));
        }

        // === Final State ===
        System.out.println("\n=== Final State ===");
        System.out.println("Countries: " + countryService.readAll());
        System.out.println("Celebrities in Italy: " + celebrityService.readByCountryId(countryId));
        System.out.println("Art in Italy: " + artService.readByCountryId(countryId));
        System.out.println("Monuments in Italy: " + monumentService.readByCountryId(countryId));
        System.out.println("Traditional Foods in Italy: " + foodService.readByCountryId(countryId));

        // Delete the country to clean up
        countryService.delete(countryId);
        System.out.println("Deleted Country ID: " + countryId);
        System.out.println("Countries after deletion: " + countryService.readAll());
    }
}