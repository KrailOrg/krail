/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.data;

import org.apache.bval.constraints.Email;
import org.apache.bval.guice.Validate;
import uk.q3c.krail.core.validation.Adult;
import uk.q3c.krail.core.validation.MalformedAnnotation;
import uk.q3c.krail.core.validation.TestValidationKey;
import uk.q3c.krail.persist.KrailEntity;

import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class TestEntity2 implements KrailEntity<Long, Integer> {

    @Min(5)
    @Max(10)
    private int age;
    @Email
    private String email;
    @NotNull
    @Size(min = 2, max = 14)
    private String firstName;
    @Min(value = 20, message = "a custom message with limit {0}")
    private int height;
    @Id
    private Long id;
    private String lastName;
    @Min(value = 5, message = "{javax.validation.constraints.Max.message}")
    private int load;
    @uk.q3c.krail.core.validation.Max(1)
    private int speed;
    @Version
    private Integer version;
    @uk.q3c.krail.core.validation.Max(value = 1, messageKey = TestValidationKey.Must_be_an_Adult)
    private int volume;
    @Max(value = 5, message = "{uk.q3c.krail.core.validation.TestValidationKey.Too_Big}")
    private int weight;
    @Max(value = 5, message = "{uk.q3ckrail.core.validation.TestValidationKey.Too_Big}")
    private int wrinkles;
    @MalformedAnnotation(value = 20)
    private int wrongAnnotation;

    public TestEntity2() {
    }

    public int getWrinkles() {
        return wrinkles;
    }

    public void setWrinkles(int wrinkles) {
        this.wrinkles = wrinkles;
    }

    public int getWrongAnnotation() {
        return wrongAnnotation;
    }

    public void setWrongAnnotation(int wrongAnnotation) {
        this.wrongAnnotation = wrongAnnotation;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Validate
    public void setANumber(@Min(value = 100) int aNumber) {

    }

    @Validate
    public void setAnAdult(@Adult(messageKey = TestValidationKey.Must_be_an_Adult, value = 18) int aNumber) {

    }
}
