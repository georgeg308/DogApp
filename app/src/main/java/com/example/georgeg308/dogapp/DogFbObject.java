package com.example.georgeg308.dogapp;

public class DogFbObject {




        public String url;
        public String breed;
        public String name;

        public DogFbObject(String url, String breed, String name) {
            this.breed = breed;
            this.name = name;
            this.url = url;
        }




        public DogFbObject() {

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }





}
