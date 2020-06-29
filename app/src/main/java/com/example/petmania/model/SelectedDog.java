package com.example.petmania.model;

import java.lang.reflect.Array;
import java.util.List;

public class SelectedDog {
    String id;
    String url;
    public List<Breeds> breeds ;

    public SelectedDog(String id, String url, List<Breeds> breeds) {
        this.id = id;
        this.url = url;
        this.breeds = breeds;
    }

    public List<Breeds> getBreeds() {
        return breeds;
    }

    public void setBreeds(List<Breeds> breeds) {
        this.breeds = breeds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public class Breeds{

        public int id;
        public String name,bred_for,breed_group,life_span,temperament,origin;
        public Height height;
        public Weight weight;

        public Breeds(int id, String name, String bred_for, String breed_group, String life_span, String temperament, String origin, Height height, Weight weight) {
            this.id = id;
            this.name = name;
            this.bred_for = bred_for;
            this.breed_group = breed_group;
            this.life_span = life_span;
            this.temperament = temperament;
            this.origin = origin;
            this.height = height;
            this.weight = weight;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBred_for() {
            return bred_for;
        }

        public void setBred_for(String bred_for) {
            this.bred_for = bred_for;
        }

        public String getBreed_group() {
            return breed_group;
        }

        public void setBreed_group(String breed_group) {
            this.breed_group = breed_group;
        }

        public String getLife_span() {
            return life_span;
        }

        public void setLife_span(String life_span) {
            this.life_span = life_span;
        }

        public String getTemperament() {
            return temperament;
        }

        public void setTemperament(String temperament) {
            this.temperament = temperament;
        }

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public Height getHeight() {
            return height;
        }

        public void setHeight(Height height) {
            this.height = height;
        }

        public Weight getWeight() {
            return weight;
        }

        public void setWeight(Weight weight) {
            this.weight = weight;
        }
    }

    public class Height {
        String imperial,metric;


        public Height(String imperial, String metric) {
            this.imperial = imperial;
            this.metric = metric;
        }

        public String getImperial() {
            return imperial;
        }

        public void setImperial(String imperial) {
            this.imperial = imperial;
        }

        public String getMetric() {
            return metric;
        }

        public void setMetric(String metric) {
            this.metric = metric;
        }
    }

    public class Weight {
        String imperial, metric;

        public Weight(String imperial, String metric) {
            this.imperial = imperial;
            this.metric = metric;
        }

        public Weight() {
        }

        public String getImperial() {
            return imperial;
        }

        public void setImperial(String imperial) {
            this.imperial = imperial;
        }

        public String getMetric() {
            return metric;
        }

        public void setMetric(String metric) {
            this.metric = metric;
        }
    }
}
